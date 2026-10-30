package org.mythic_goose.amalgamation.api.registry_v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Registration helper bound to a single vanilla {@link Registry}. Create one per
 * registry you populate (blocks, items, sounds, potions, ...), then call
 * {@link #register(String, Supplier)} for each entry. Actual registration is routed
 * through {@link RegistrationBackends}, so it behaves identically on every supported
 * loader.
 *
 * <h2>Setting the mod id (once per mod)</h2>
 * <p>Call this once from each mod's main entrypoint, before any registry class is touched:
 * <pre>{@code DeferredRegister.setModId(YourMod.MOD_ID); }</pre>
 * The id is tied to the <em>package</em> of the class that calls it (and its
 * sub-packages), so several mods can each call it once without clashing. This means your
 * entrypoint should live in your mod's root package, e.g. {@code com.you.yourmod}, with
 * your registry classes in that package or below it.
 *
 * <p>After that, just use {@link #create(Registry)} and {@link #id(String)} anywhere in
 * your mod; the right id is picked automatically from the calling class.
 *
 * <p>For registries this class doesn't wrap directly - datapack registries like
 * enchantments, or vanilla registries you register into by hand, like particle types -
 * use the static {@link #id(String)} to build a correctly-namespaced id.
 *
 * <p>If you'd rather not rely on packages, the explicit overloads
 * {@link #create(String, Registry)} and {@link #id(String, String)} take the mod id directly.
 */
public final class DeferredRegister<T> {

    /** Root package of a mod's entrypoint -> that mod's id. */
    private static final Map<String, String> MOD_IDS = new ConcurrentHashMap<>();

    private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private final String modId;
    private final Registry<? super T> registry;
    private final List<RegistryEntry<T>> entries = new ArrayList<>();

    private DeferredRegister(String modId, Registry<? super T> registry) {
        this.modId = Objects.requireNonNull(modId, "modId");
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    // ---------------------------------------------------------------- mod id

    /**
     * Declares the mod id for the calling class's package and everything below it. Call
     * once per mod, from its main entrypoint, before any registration happens.
     *
     * @throws IllegalStateException if this package already has a different mod id
     */
    public static void setModId(String modId) {
        Objects.requireNonNull(modId, "modId");
        String pkg = packageOf(callerClass());
        String existing = MOD_IDS.putIfAbsent(pkg, modId);
        if (existing != null && !existing.equals(modId)) {
            throw new IllegalStateException("Mod id for package '" + pkg + "' already set to '"
                    + existing + "', cannot change it to '" + modId + "'");
        }
    }

    /** The mod id this helper registers under. */
    public String getModId() {
        return modId;
    }

    private static Class<?> callerClass() {
        return WALKER.walk(frames -> frames
                .map(StackWalker.StackFrame::getDeclaringClass)
                .filter(c -> c != DeferredRegister.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not determine calling class")));
    }

    private static String packageOf(Class<?> c) {
        return c.getPackageName();
    }

    /** Finds the mod id for a calling class: longest matching package prefix wins. */
    private static String resolveModId(Class<?> caller) {
        String callerPkg = packageOf(caller);
        String bestPkg = null;
        for (String pkg : MOD_IDS.keySet()) {
            boolean matches = callerPkg.equals(pkg) || callerPkg.startsWith(pkg + ".");
            if (matches && (bestPkg == null || pkg.length() > bestPkg.length())) {
                bestPkg = pkg;
            }
        }
        if (bestPkg != null) {
            return MOD_IDS.get(bestPkg);
        }

        // Fallback: if only one distinct mod id has been declared, there's no ambiguity.
        Set<String> distinct = new HashSet<>(MOD_IDS.values());
        if (distinct.size() == 1) {
            return distinct.iterator().next();
        }

        throw new IllegalStateException("No mod id found for class '" + caller.getName()
                + "'. Call DeferredRegister.setModId(...) once from your mod's main entrypoint, "
                + "and keep your registry classes in that entrypoint's package or a sub-package.");
    }

    // ------------------------------------------------------------------- ids

    /** Builds a namespaced id for the calling mod (resolved from the calling class's package). */
    public static Identifier id(String path) {
        return id(resolveModId(callerClass()), path);
    }

    /** Builds a namespaced id with an explicit mod id. */
    public static Identifier id(String modId, String path) {
        return Identifier.fromNamespaceAndPath(modId, path);
    }

    // ---------------------------------------------------------------- create

    /**
     * Creates a helper bound to the given vanilla registry, using the mod id declared via
     * {@link #setModId(String)} for the calling class's package.
     *
     * @param registry accepts a registry of any supertype of {@code T}, so e.g. items
     *                 and block-items can share one {@code DeferredRegister<Item>}
     *                 bound to {@code BuiltInRegistries.ITEM}
     */
    public static <T> DeferredRegister<T> create(Registry<? super T> registry) {
        return new DeferredRegister<>(resolveModId(callerClass()), registry);
    }

    /** Creates a helper bound to the given registry with an explicit mod id. */
    public static <T> DeferredRegister<T> create(String modId, Registry<? super T> registry) {
        return new DeferredRegister<>(modId, registry);
    }

    // -------------------------------------------------------------- register

    /**
     * Registers {@code factory} under {@code path}. {@code U} may be any subtype of
     * {@code T} - e.g. a {@code DeferredRegister<Item>} bound to the item registry can
     * register a {@code BlockItem} and hand back a {@code RegistryEntry<BlockItem>}.
     */
    @SuppressWarnings("unchecked")
    public <U extends T> RegistryEntry<U> register(String path, Supplier<U> factory) {
        RegistryEntry<U> entry = RegistrationBackends.get().register((Registry<U>) registry, id(modId, path), factory);
        entries.add((RegistryEntry<T>) entry);
        return entry;
    }

    /**
     * All entries registered through this helper, in registration order. Don't resolve
     * these (via {@code .get()}) before your loader's registration phase has actually run.
     */
    public List<RegistryEntry<T>> all() {
        return Collections.unmodifiableList(entries);
    }
}
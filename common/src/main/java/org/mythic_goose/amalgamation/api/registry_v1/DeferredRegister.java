package org.mythic_goose.amalgamation.api.registry_v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Registration helper bound to a single vanilla {@link Registry}. Create one per
 * registry you populate (blocks, items, sounds, potions, ...), then call
 * {@link #register(String, Supplier)} for each entry. Actual registration is routed
 * through {@link RegistrationBackends}, so it behaves identically on every supported
 * loader.
 *
 * <h2>Multiple mods, one library</h2>
 * Any number of mods can use this class at the same time, each with its own mod id.
 * Every mod is treated as a separate owner and is identified by:
 * <ol>
 *   <li><b>The jar (or build output folder) its classes are loaded from.</b></li>
 *   <li><b>The package of its entrypoint</b> (and sub-packages), as a fallback.
 *       The longest matching package wins.</li>
 * </ol>
 * If the calling class cannot be matched to a declared mod, an
 * {@link IllegalStateException} is thrown.
 *
 * <h2>Ownership tracking</h2>
 * Every registered id is recorded together with the mod that registered it. Registering
 * the same {@code registry + id} twice throws, naming both mods and the calling class,
 * so cross-mod collisions are reported instead of silently misbehaving.
 *
 * <p>The explicit overloads {@link #create(String, Registry)} and {@link #id(String, String)}
 * take the mod id directly and skip resolution entirely.
 */
public final class DeferredRegister<T> {

    /** Package (root of a mod's code) -> that mod's id. */
    private static final Map<String, String> PACKAGE_IDS = new ConcurrentHashMap<>();

    /** Code source (jar or output folder) -> that mod's id. */
    private static final Map<String, String> SOURCE_IDS = new ConcurrentHashMap<>();

    /**
     * "registryKey|namespace:path" -> owning mod id. The registry is part of the key because
     * a block and its item legitimately share a path.
     */
    private static final Map<String, String> REGISTERED = new ConcurrentHashMap<>();

    private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    /**
     * Package holding this library's own registry helpers. Frames from this package and its
     * sub-packages are skipped when working out who the "real" caller is.
     */
    private static final String LIB_PACKAGE = DeferredRegister.class.getPackageName();

    private static final System.Logger LOG = System.getLogger("amalgamation/DeferredRegister");

    private static void trace(String message) {
        LOG.log(System.Logger.Level.INFO, message);
    }

    private final String modId;
    private final Registry<? super T> registry;
    private final List<RegistryEntry<T>> entries = new ArrayList<>();

    private DeferredRegister(String modId, Registry<? super T> registry) {
        this.modId = Objects.requireNonNull(modId, "modId");
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    // ---------------------------------------------------------------- mod id

    /**
     * Declares the mod id for the calling class's jar and package. Call once per mod,
     * from its main entrypoint, before any registration happens.
     *
     * @param modId the mod id to associate with the caller
     * @param extraPackages additional packages (and their sub-packages) that also belong to
     *                      this mod, for registry classes outside the entrypoint's package
     * @throws IllegalStateException if the jar or a package is already owned by a different mod id
     */
    public static void setModId(String modId, String... extraPackages) {
        bindMod(modId, extraPackages);
    }

    /**
     * Declares the mod id for the calling class's jar and package. Kept as its own
     * method so mods compiled against earlier versions of this library keep linking.
     *
     * @param modId the mod id to associate with the caller
     * @throws IllegalStateException if the jar or package is already owned by a different mod id
     */
    public static void setModId(String modId) {
        bindMod(modId, null);
    }

    private static void bindMod(String modId, String[] extraPackages) {
        Objects.requireNonNull(modId, "modId");
        Class<?> caller = callerClass();
        trace("setModId('" + modId + "') called from " + caller.getName()
                + " source=" + sourceOf(caller)
                + " extraPackages=" + (extraPackages == null ? "[]" : Arrays.toString(extraPackages)));

        bind(PACKAGE_IDS, caller.getPackageName(), modId, "Package");

        String source = sourceOf(caller);
        if (source != null) {
            bind(SOURCE_IDS, source, modId, "Code source");
        }

        if (extraPackages != null) {
            for (String pkg : extraPackages) {
                bind(PACKAGE_IDS, Objects.requireNonNull(pkg, "extraPackages entry"), modId, "Package");
            }
        }
    }

    private static void bind(Map<String, String> map, String key, String modId, String what) {
        String existing = map.putIfAbsent(key, modId);
        if (existing != null && !existing.equals(modId)) {
            throw new IllegalStateException(what + " '" + key + "' already belongs to mod '"
                    + existing + "', cannot also assign it to '" + modId + "'");
        }
    }

    /**
     * The mod id this helper registers under.
     *
     * @return the mod id
     */
    public String getModId() {
        return modId;
    }

    private static boolean isLibraryClass(Class<?> c) {
        String p = c.getPackageName();
        return p.equals(LIB_PACKAGE) || p.startsWith(LIB_PACKAGE + ".");
    }

    private static Class<?> callerClass() {
        return WALKER.walk(frames -> frames
                .map(StackWalker.StackFrame::getDeclaringClass)
                .filter(c -> !isLibraryClass(c))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not determine calling class")));
    }

    /** The jar or folder a class was loaded from, or null if it can't be determined. */
    private static String sourceOf(Class<?> c) {
        try {
            CodeSource cs = c.getProtectionDomain().getCodeSource();
            if (cs == null) {
                return null;
            }
            URL location = cs.getLocation();
            return location == null ? null : location.toString();
        } catch (SecurityException e) {
            return null;
        }
    }

    /**
     * Finds the mod id for a calling class: exact code source first, then longest matching
     * declared package. Throws if neither matches.
     */
    private static String resolveModId(Class<?> caller) {
        String source = sourceOf(caller);
        if (source != null) {
            String bySource = SOURCE_IDS.get(source);
            if (bySource != null) {
                return bySource;
            }
        }

        String callerPkg = caller.getPackageName();
        String bestPkg = null;
        for (String pkg : PACKAGE_IDS.keySet()) {
            boolean matches = callerPkg.equals(pkg) || callerPkg.startsWith(pkg + ".");
            if (matches && (bestPkg == null || pkg.length() > bestPkg.length())) {
                bestPkg = pkg;
            }
        }
        if (bestPkg != null) {
            return PACKAGE_IDS.get(bestPkg);
        }

        throw new IllegalStateException("No mod id found for class '" + caller.getName()
                + "'. Call DeferredRegister.setModId(...) from your mod's main entrypoint "
                + "before any registry class loads, or use the overloads that take the mod id explicitly.");
    }

    // ------------------------------------------------------------------- ids

    /**
     * Builds a namespaced id for the calling mod (resolved from the calling class).
     *
     * @param path the id path
     * @return the namespaced identifier
     */
    public static Identifier id(String path) {
        return id(resolveModId(callerClass()), path);
    }

    /**
     * Builds a namespaced id with an explicit mod id.
     *
     * @param modId the namespace
     * @param path the id path
     * @return the namespaced identifier
     */
    public static Identifier id(String modId, String path) {
        return Identifier.fromNamespaceAndPath(modId, path);
    }

    // ---------------------------------------------------------------- create

    /**
     * Creates a helper bound to the given vanilla registry, using the mod id declared via
     * {@link #setModId(String, String...)} for the calling class.
     *
     * @param registry accepts a registry of any supertype of {@code T}
     * @param <T> the registered type
     * @return a new helper
     */
    public static <T> DeferredRegister<T> create(Registry<? super T> registry) {
        Class<?> caller = callerClass();
        String resolved = resolveModId(caller);
        trace("create(" + registry.key() + ") from " + caller.getName()
                + " source=" + sourceOf(caller) + " -> modId='" + resolved + "'");
        return new DeferredRegister<>(resolved, registry);
    }

    /**
     * Creates a helper bound to the given registry with an explicit mod id. Several helpers
     * with different mod ids can coexist freely.
     *
     * @param modId the mod id to register under
     * @param registry the registry to bind to
     * @param <T> the registered type
     * @return a new helper
     */
    public static <T> DeferredRegister<T> create(String modId, Registry<? super T> registry) {
        Class<?> caller = callerClass();
        trace("create(" + registry.key() + ") with explicit modId='" + modId + "' from " + caller.getName()
                + " source=" + sourceOf(caller));
        return new DeferredRegister<>(modId, registry);
    }

    // -------------------------------------------------------------- register

    /**
     * Registers {@code factory} under {@code path}. {@code U} may be any subtype of
     * {@code T} - e.g. a {@code DeferredRegister<Item>} bound to the item registry can
     * register a {@code BlockItem} and hand back a {@code RegistryEntry<BlockItem>}.
     *
     * @param path the id path
     * @param factory supplies the object to register
     * @param <U> the concrete registered type
     * @return the registry entry
     * @throws IllegalStateException if the same registry + id was already registered
     */
    @SuppressWarnings("unchecked")
    public <U extends T> RegistryEntry<U> register(String path, Supplier<U> factory) {
        Identifier id = id(modId, path);
        String key = registry.key() + "|" + id;
        Class<?> caller = callerClass();

        String owner = REGISTERED.putIfAbsent(key, modId);
        if (owner != null) {
            throw new IllegalStateException("Duplicate registration " + key
                    + ": already registered by mod '" + owner + "', now requested by mod '"
                    + modId + "' from " + caller.getName() + " (source=" + sourceOf(caller) + ")");
        }
        trace("register " + key + " (mod '" + modId + "') from " + caller.getName()
                + " source=" + sourceOf(caller));

        RegistryEntry<U> entry;
        try {
            entry = RegistrationBackends.get().register((Registry<U>) registry, id, factory);
        } catch (RuntimeException | Error e) {
            REGISTERED.remove(key, modId);
            throw e;
        }
        entries.add((RegistryEntry<T>) entry);
        return entry;
    }

    /**
     * All entries registered through this helper, in registration order. Don't resolve
     * these (via {@code .get()}) before your loader's registration phase has actually run.
     *
     * @return an unmodifiable view of the entries
     */
    public List<RegistryEntry<T>> all() {
        return Collections.unmodifiableList(entries);
    }

    // ------------------------------------------------------- per-mod registers

    /** (modId + registry) -> shared register for that mod. */
    private static final Map<String, DeferredRegister<?>> PER_MOD = new ConcurrentHashMap<>();

    /**
     * Returns the register for the mod that is calling, creating it on first use. Used by the
     * *Registry base classes so each mod gets its own register instead of sharing one.
     * Callers in this library's package are skipped, so the "caller" is the mod's own class
     * (e.g. NSBlocks).
     */
    @SuppressWarnings("unchecked")
    public static <T> DeferredRegister<T> forCallingMod(Registry<? super T> registry) {
        String modId = resolveModId(callerClass());
        return (DeferredRegister<T>) PER_MOD.computeIfAbsent(
                modId + "|" + registry.key(),
                k -> new DeferredRegister<>(modId, registry));
    }
}
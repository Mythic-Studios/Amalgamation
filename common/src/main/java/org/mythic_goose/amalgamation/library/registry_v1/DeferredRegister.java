package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Registration helper bound to a single vanilla {@link Registry}. Create one per
 * registry you populate (blocks, items, sounds, potions, ...), then call
 * {@link #register(String, Supplier)} for each entry. Actual registration is routed
 * through {@link RegistrationBackends}, so it behaves identically on every supported
 * loader.
 *
 * <p>For registries this class doesn't wrap directly - datapack registries like
 * enchantments, or vanilla registries you register into by hand, like particle types -
 * use the static {@link #id(String)} to build a correctly-namespaced id without needing
 * an instance.
 *
 *
 * <h2>Setting the mod id</h2>
 * <p>Once, from your loader's main entrypoint, before any registry class is touched:
 * <pre>{@code DeferredRegister.setModId(YourMod.MOD_ID); }</pre>
 */
public final class DeferredRegister<T> {

    /** Set once, from the mod's main entrypoint, before any registration occurs. */
    private static String MOD_ID;

    private final Registry<? super T> registry;
    private final List<RegistryEntry<T>> entries = new ArrayList<>();

    private DeferredRegister(Registry<? super T> registry) {
        this.registry = registry;
    }

    /**
     * Sets the active mod id. Call exactly once, from the main entrypoint, before any
     * registration happens.
     *
     * @throws IllegalStateException if called more than once with a different value
     */
    public static void setModId(String modId) {
        if (MOD_ID != null && !MOD_ID.equals(modId)) {
            throw new IllegalStateException(
                    "Mod id already set to '" + MOD_ID + "', cannot change it to '" + modId + "'");
        }
        MOD_ID = modId;
    }

    public static String getModId() {
        if (MOD_ID == null) {
            throw new IllegalStateException(
                    "DeferredRegister.setModId(...) must be called before any registration occurs");
        }
        return MOD_ID;
    }

    /** Builds a namespaced id, for registries that don't go through a bound {@code DeferredRegister}. */
    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(getModId(), path);
    }

    /**
     * Creates a helper bound to the given vanilla registry.
     *
     * @param registry accepts a registry of any supertype of {@code T}, so e.g. items
     *                 and block-items can share one {@code DeferredRegister<Item>}
     *                 bound to {@code BuiltInRegistries.ITEM}
     */
    public static <T> DeferredRegister<T> create(Registry<? super T> registry) {
        return new DeferredRegister<>(registry);
    }

    /**
     * Registers {@code factory} under {@code path}. {@code U} may be any subtype of
     * {@code T} - e.g. a {@code DeferredRegister<Item>} bound to the item registry can
     * register a {@code BlockItem} and hand back a {@code RegistryEntry<BlockItem>}.
     */
    @SuppressWarnings("unchecked")
    public <U extends T> RegistryEntry<U> register(String path, Supplier<U> factory) {
        RegistryEntry<U> entry = RegistrationBackends.get().register((Registry<U>) registry, id(path), factory);
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
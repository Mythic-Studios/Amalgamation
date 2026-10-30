package org.mythic_goose.amalgamation.api.registry_v1;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * NeoForge defers actual registration until each registry's {@code RegisterEvent}
 * fires. Each mod must call {@link #init(String, IEventBus)} once, in its constructor,
 * with its own mod id and event bus, before registering anything.
 *
 * <p>Deferred registers and event buses are partitioned per mod namespace, so several
 * mods using this library never share a namespace or a bus.
 */
public class NeoForgeRegistrationBackend implements RegistrationBackend {

    /** mod id (namespace) -> that mod's event bus. */
    private static final Map<String, IEventBus> BUSES = new ConcurrentHashMap<>();

    /** Used only by the legacy single-argument {@link #init(IEventBus)}. */
    private static volatile IEventBus fallbackBus;

    private record Key(ResourceKey<? extends Registry<?>> registry, String namespace) {}

    private final Map<Key, DeferredRegister<?>> deferredRegisters = new ConcurrentHashMap<>();

    /**
     * Call from each mod's constructor:
     * {@code NeoForgeRegistrationBackend.init(MOD_ID, modEventBus);}
     */
    public static void init(String modId, IEventBus bus) {
        BUSES.put(modId, bus);
    }

    /**
     * @deprecated Cannot tell mods apart. Only used as a fallback for namespaces that never
     *             called {@link #init(String, IEventBus)}. Prefer the two-argument version.
     */
    @Deprecated
    public static void init(IEventBus bus) {
        fallbackBus = bus;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> RegistryEntry<T> register(Registry<T> registry, Identifier id, Supplier<T> factory) {
        String namespace = id.getNamespace();
        IEventBus bus = BUSES.getOrDefault(namespace, fallbackBus);
        if (bus == null) {
            throw new IllegalStateException(
                    "No event bus for mod '" + namespace + "'. Call "
                            + "NeoForgeRegistrationBackend.init(\"" + namespace + "\", modEventBus) in your mod's "
                            + "constructor before registering \"" + id + "\"");
        }

        Key key = new Key(registry.key(), namespace);
        DeferredRegister<T> deferred = (DeferredRegister<T>) deferredRegisters.computeIfAbsent(key, k -> {
            DeferredRegister<T> created =
                    DeferredRegister.create((ResourceKey<Registry<T>>) registry.key(), namespace);
            created.register(bus);
            return created;
        });

        DeferredHolder<T, T> holder = deferred.register(id.getPath(), factory);
        return new RegistryEntry<T>() {
            @Override public T get() { return holder.get(); }
            @Override public Identifier id() { return id; }
            @Override public Holder<T> asHolder() { return holder; }
        };
    }
}
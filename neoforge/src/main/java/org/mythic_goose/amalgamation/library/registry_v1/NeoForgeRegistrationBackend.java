package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mythic_goose.amalgamation.library.registry_v1.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * NeoForge defers actual registration until each registry's {@code RegisterEvent}
 * fires. Call {@link #init(IEventBus)} once, in your mod's constructor, before
 * registering anything through the common {@code *Registry} helpers.
 */
public class NeoForgeRegistrationBackend implements RegistrationBackend {

    private static volatile IEventBus modEventBus;
    private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> deferredRegisters = new ConcurrentHashMap<>();

    /** Call from your mod's constructor: {@code NeoForgeRegistrationBackend.init(modEventBus);} */
    public static void init(IEventBus modEventBus) {
        NeoForgeRegistrationBackend.modEventBus = modEventBus;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> RegistryEntry<T> register(Registry<T> registry, Identifier id, Supplier<T> factory) {
        IEventBus bus = modEventBus;
        if (bus == null) {
            throw new IllegalStateException(
                    "NeoForgeRegistrationBackend.init(modEventBus) must be called in your mod's "
                            + "constructor before registering \"" + id + "\"");
        }

        DeferredRegister<T> deferred = (DeferredRegister<T>) deferredRegisters.computeIfAbsent(registry.key(), key -> {
            DeferredRegister<T> created = DeferredRegister.create((ResourceKey<Registry<T>>) key, id.getNamespace());
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
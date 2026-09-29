package org.mythic_goose.amalgamation.api.registry_v1;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

/**
 * Fabric registers immediately - {@code Registry.register} is safe to call anytime
 * during mod init, so the factory is just invoked right away.
 */
public class FabricRegistrationBackend implements RegistrationBackend {

    @Override
    public <T> RegistryEntry<T> register(Registry<T> registry, Identifier id, Supplier<T> factory) {
        T value = Registry.register(registry, id, factory.get());
        ResourceKey<T> key = ResourceKey.create(registry.key(), id);
        return new RegistryEntry<T>() {
            @Override public T get() { return value; }
            @Override public Identifier id() { return id; }
            @Override public Holder<T> asHolder() { return registry.getOrThrow(key); }
        };
    }
}
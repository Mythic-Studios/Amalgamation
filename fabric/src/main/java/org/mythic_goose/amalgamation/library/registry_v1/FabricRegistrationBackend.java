package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

/**
 * Fabric registers immediately - {@code Registry.register} is safe to call anytime
 * during mod init, so the factory is just invoked right away.
 */
public class FabricRegistrationBackend implements RegistrationBackend {

    @Override
    public <T> RegistryEntry<T> register(Registry<T> registry, Identifier id, Supplier<T> factory) {
        T value = Registry.register(registry, id, factory.get());
        return new RegistryEntry<T>() {
            @Override public T get() { return value; }
            @Override public Identifier id() { return id; }
        };
    }
}
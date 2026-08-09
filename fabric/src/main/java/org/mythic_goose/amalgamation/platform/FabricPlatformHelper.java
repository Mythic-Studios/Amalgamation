package org.mythic_goose.amalgamation.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.mythic_goose.amalgamation.library.registry_v1.RegistryEntry;
import org.mythic_goose.amalgamation.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T> RegistryEntry<T> register(Registry<? super T> registry, Identifier id, Supplier<T> factory) {
        T value = factory.get();
        Registry.register(registry, id, value);
        return new RegistryEntry<T>() {
            @Override public T get() { return value; }
            @Override public Identifier id() { return id; }
        };
    }
}
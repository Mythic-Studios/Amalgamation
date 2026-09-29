package org.mythic_goose.amalgamation.platform;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mythic_goose.amalgamation.api.registry_v1.RegistryEntry;
import org.mythic_goose.amalgamation.platform.services.IPlatformHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class NeoForgePlatformHelper implements IPlatformHelper {

    // ServiceLoader instantiates this via a no-arg constructor, so the event bus can't be
    // constructor-injected - call init(modEventBus) from your mod's constructor instead,
    // before registering anything through the *Registry helpers.
    private static volatile IEventBus modEventBus;

    private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> deferredRegisters = new ConcurrentHashMap<>();

    public static void init(IEventBus modEventBus) {
        NeoForgePlatformHelper.modEventBus = modEventBus;
    }

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> RegistryEntry<T> register(Registry<? super T> registry, Identifier id, Supplier<T> factory) {
        IEventBus bus = modEventBus;
        if (bus == null) {
            throw new IllegalStateException(
                    "NeoForgePlatformHelper.init(modEventBus) must be called in your mod's "
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
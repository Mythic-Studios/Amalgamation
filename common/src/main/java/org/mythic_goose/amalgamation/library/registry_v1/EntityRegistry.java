package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.function.Function;

public abstract class EntityRegistry {

    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE);

    @SuppressWarnings("unchecked")
    protected static <T extends Entity> RegistryEntry<EntityType<T>> registerEntity(
            String path, Function<ResourceKey<EntityType<T>>, EntityType<T>> factory) {

        ResourceKey<Registry<EntityType<T>>> registryKey =
                (ResourceKey<Registry<EntityType<T>>>) (ResourceKey<?>) BuiltInRegistries.ENTITY_TYPE.key();

        ResourceKey<EntityType<T>> key = ResourceKey.create(registryKey, DeferredRegister.id(path));

        return (RegistryEntry<EntityType<T>>) (RegistryEntry<?>)
                ENTITIES.register(path, (java.util.function.Supplier<EntityType<?>>) () -> factory.apply(key));
    }

    public static List<RegistryEntry<EntityType<?>>> all() {
        return ENTITIES.all();
    }
}
package org.mythic_goose.amalgamation.api.registry_v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.function.Supplier;

/** Registry helper for block entity types. Each mod that extends this class gets its own register. */
public abstract class BlockEntityRegistry {

    private static DeferredRegister<BlockEntityType<?>> blockEntities() {
        return DeferredRegister.forCallingMod(BuiltInRegistries.BLOCK_ENTITY_TYPE);
    }

    @SuppressWarnings("unchecked")
    protected static <T extends BlockEntity> RegistryEntry<BlockEntityType<T>> registerBlockEntity(
            String path, Supplier<BlockEntityType<T>> factory) {
        return (RegistryEntry<BlockEntityType<T>>) (RegistryEntry<?>)
                blockEntities().register(path, (Supplier<BlockEntityType<?>>) (Supplier<?>) factory);
    }

    /** All block entity types registered by the calling mod. */
    public static List<RegistryEntry<BlockEntityType<?>>> all() {
        return blockEntities().all();
    }
}
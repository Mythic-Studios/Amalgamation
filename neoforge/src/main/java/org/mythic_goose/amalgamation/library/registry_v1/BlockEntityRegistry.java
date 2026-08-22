package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.function.Supplier;

/**
 * Registry helper for block entity types. Works identically on Fabric and NeoForge.
 *
 * <p>Extend this class, declare your block entity types as static fields using
 * {@link #registerBlockEntity(String, Supplier)}, then call a triggering method from
 * your loader's entrypoint to force class-loading and register them all.
 *
 * <h2>Important</h2>
 * <p>The {@code factory} supplier you pass in is only invoked during your loader's
 * registration phase — never at class-load time. This means it's safe to call
 * {@code .get()} on other {@link RegistryEntry}s (like the block this block entity
 * belongs to) inside the supplier, since by the time it actually runs, those entries
 * are guaranteed to be bound.
 *
 * <h2>Quick start</h2>
 * <pre>{@code
 * public class AmalgamationBlockEntities extends BlockEntityRegistry {
 *
 *     public static final RegistryEntry<BlockEntityType<MyBlockEntity>> MY_BE =
 *         registerBlockEntity("my_block_entity", () ->
 *             CommonBlockEntityTypeBuilder.create(MyBlockEntity::new, MY_BLOCK.get()).build());
 *
 *     public static void registerModBlockEntities() {} // triggers static field loading
 * }
 * }</pre>
 */
public abstract class BlockEntityRegistry {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE);

    /**
     * Registers a block entity type. {@code factory} is only invoked during the
     * registration phase — safe to resolve other RegistryEntries inside it.
     */
    @SuppressWarnings("unchecked")
    protected static <T extends BlockEntity> RegistryEntry<BlockEntityType<T>> registerBlockEntity(
            String path, Supplier<BlockEntityType<T>> factory) {
        return (RegistryEntry<BlockEntityType<T>>) (RegistryEntry<?>) BLOCK_ENTITIES.register(path, (Supplier<BlockEntityType<?>>) (Supplier<?>) factory);
    }

    /**
     * All block entity types registered through this helper, in registration order.
     * Don't resolve these (via {@code .get()}) before your loader's registration
     * phase has actually run.
     */
    public static List<RegistryEntry<BlockEntityType<?>>> all() {
        return BLOCK_ENTITIES.all();
    }
}
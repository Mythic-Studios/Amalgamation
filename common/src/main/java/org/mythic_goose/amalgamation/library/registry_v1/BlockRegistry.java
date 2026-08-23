package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.Function;

/**
 * Registry helper for blocks. Works identically on Fabric and NeoForge.
 *
 * <p>Extend this class, declare your blocks as static fields, then call a triggering
 * method from your loader's entrypoint to force class-loading and register them all.
 *
 * <h2>Quick start</h2>
 * <pre>{@code
 * public class AmalgamationBlocks extends BlockRegistry {
 *
 *     public static final RegistryEntry<Block> RUBY_ORE = registerBlock(
 *         "ruby_ore",
 *         properties -> new Block(properties.mapColor(MapColor.COLOR_RED).requiresCorrectToolForDrops())
 *     );
 *
 *     public static void registerModBlocks() {} // triggers static field loading
 * }
 * }</pre>
 *
 * Then in your entrypoint:
 * <pre>{@code AmalgamationBlocks.registerModBlocks(); }</pre>
 *
 * <h2>Block IDs</h2>
 * <p>The resulting in-game id is {@code [Constants.MOD_ID]:[path]}, e.g. {@code amalgamation:ruby_ore}.
 *
 * <h2>Getting the block</h2>
 * <p>{@code RUBY_ORE} is a {@link RegistryEntry}, not a raw {@code Block} - call
 * {@code RUBY_ORE.get()} at runtime, not from another class's static initializer
 * (which may run before registration completes on NeoForge).
 *
 * @see ItemRegistry for registering the block's item form via registerBlockItem
 */
public abstract class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK);

    /**
     * Registers a block and tracks it internally.
     *
     * @param path    block name in snake_case
     * @param factory builds the block from properties already carrying its id
     * @return the registered block entry
     */
    protected static RegistryEntry<Block> registerBlock(String path, Function<BlockBehaviour.Properties, Block> factory) {
        ResourceKey<Block> key = ResourceKey.create(BuiltInRegistries.BLOCK.key(), DeferredRegister.id(path));
        return BLOCKS.register(path, () -> factory.apply(BlockBehaviour.Properties.of().setId(key)));
    }

    /**
     * All blocks registered through this helper, in registration order. Useful for
     * bulk-registering block items. Don't resolve these (via {@code .get()}) before
     * your loader's registration phase has actually run.
     */
    public static List<RegistryEntry<Block>> all() {
        return BLOCKS.all();
    }

    /**
     * @param block the resolved block you want the resource key for - used for BlockTags
     */
    public static ResourceKey<Block> getRK(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).get();
    }
}
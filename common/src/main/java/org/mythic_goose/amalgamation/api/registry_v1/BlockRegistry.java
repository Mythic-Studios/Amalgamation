package org.mythic_goose.amalgamation.api.registry_v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.Function;

/**
 * Registry helper for blocks. Works identically on Fabric and NeoForge.
 * Each mod that extends this class gets its own register, so ids never leak between mods.
 *
 * @see ItemRegistry for registering the block's item form via registerBlockItem
 */
public abstract class BlockRegistry {

    /** The calling mod's block register. */
    protected static DeferredRegister<Block> blocks() {
        return DeferredRegister.forCallingMod(BuiltInRegistries.BLOCK);
    }

    protected static RegistryEntry<Block> registerBlock(String path, Function<BlockBehaviour.Properties, Block> factory) {
        ResourceKey<Block> key = ResourceKey.create(BuiltInRegistries.BLOCK.key(), DeferredRegister.id(path));
        return blocks().register(path, () -> factory.apply(BlockBehaviour.Properties.of().setId(key)));
    }

    /** All blocks registered by the calling mod, in registration order. */
    public static List<RegistryEntry<Block>> all() {
        return blocks().all();
    }

    public static ResourceKey<Block> getRK(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).get();
    }
}
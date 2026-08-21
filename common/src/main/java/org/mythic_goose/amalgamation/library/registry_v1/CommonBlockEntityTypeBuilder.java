package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Loader-agnostic replacement for FabricBlockEntityTypeBuilder.
 * Built entirely on vanilla classes — safe to use in common/multiloader code
 * without a Fabric API dependency.
 */
public final class CommonBlockEntityTypeBuilder<T extends BlockEntity> {

    private final BlockEntityType.BlockEntitySupplier<? extends T> factory;
    private final Set<Block> blocks = new LinkedHashSet<>();

    private CommonBlockEntityTypeBuilder(BlockEntityType.BlockEntitySupplier<? extends T> factory, Block... initialBlocks) {
        this.factory = factory;
        for (Block b : initialBlocks) {
            blocks.add(b);
        }
    }

    public static <T extends BlockEntity> CommonBlockEntityTypeBuilder<T> create(
            BlockEntityType.BlockEntitySupplier<? extends T> factory, Block... blocks) {
        // Explicit <T> instead of diamond <> — with diamond, javac captures
        // the wildcard as a fresh type distinct from T, which doesn't match
        // the declared CommonBlockEntityTypeBuilder<T> return type.
        return new CommonBlockEntityTypeBuilder<T>(factory, blocks);
    }

    public CommonBlockEntityTypeBuilder<T> addBlock(Block block) {
        blocks.add(block);
        return this;
    }

    public CommonBlockEntityTypeBuilder<T> addBlocks(Block... moreBlocks) {
        for (Block b : moreBlocks) {
            blocks.add(b);
        }
        return this;
    }

    public BlockEntityType<T> build() {
        // No more BlockEntityType.Builder in this version — the constructor
        // is public and takes the factory + validBlocks set directly.
        return new BlockEntityType<>(factory, blocks);
    }
}
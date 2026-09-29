package org.mythic_goose.amalgamation.testmod;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import org.mythic_goose.amalgamation.api.registry_v1.BlockRegistry;
import org.mythic_goose.amalgamation.api.registry_v1.RegistryEntry;

public class TestBlocks extends BlockRegistry {

    public static final RegistryEntry<Block> TEST_ORE = registerBlock(
            "test_ore",
            properties -> new Block(properties.mapColor(MapColor.COLOR_RED).requiresCorrectToolForDrops())
    );

    /** Triggers static field loading - call once from the testmod's entrypoint. */
    public static void registerTestBlocks() {}
}
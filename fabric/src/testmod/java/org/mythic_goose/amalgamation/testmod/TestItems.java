package org.mythic_goose.amalgamation.testmod;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.mythic_goose.amalgamation.library.registry_v1.ItemRegistry;
import org.mythic_goose.amalgamation.library.registry_v1.RegistryEntry;

public class TestItems extends ItemRegistry {

    public static final RegistryEntry<Item> TEST_RUBY = registerItem("test_ruby", Item::new);

    // References TestBlocks.TEST_ORE, which triggers TestBlocks' static init too.
    public static final RegistryEntry<BlockItem> TEST_ORE_ITEM =
            registerBlockItem("test_ore", TestBlocks.TEST_ORE);

    /** Triggers static field loading - call once from the testmod's entrypoint. */
    public static void registerTestItems() {}
}
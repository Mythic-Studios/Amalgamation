package org.mythic_goose.amalgamation.api.registry_v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

/**
 * Registry helper for items. Works identically on Fabric and NeoForge.
 *
 * <p>Extend this class, declare your items as static fields, then call a triggering
 * method from your loader's entrypoint to force class-loading and register them all.
 *
 * <h2>Quick start — standalone item</h2>
 * <pre>{@code
 * public class AmalgamationItems extends ItemRegistry {
 *
 *     public static final RegistryEntry<Item> RUBY = registerItem(
 *         "ruby",
 *         properties -> new Item(properties)
 *     );
 *
 *     public static void registerModItems() {} // triggers static field loading
 * }
 * }</pre>
 *
 * <h2>Quick start — block item</h2>
 * <p>To give a block an item form (so it appears in inventories), use
 * {@link #registerBlockItem}, passing the {@link RegistryEntry} the block was
 * registered with:
 * <pre>{@code
 * public static final RegistryEntry<BlockItem> RUBY_ORE_ITEM =
 *     registerBlockItem("ruby_ore", AmalgamationBlocks.RUBY_ORE);
 * }</pre>
 */
public abstract class ItemRegistry {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM);

    /**
     * Registers a custom item.
     *
     * @param path    item name in snake_case
     * @param factory builds the item from properties already carrying its id
     * @return the registered item entry
     */
    protected static RegistryEntry<Item> registerItem(String path, Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), DeferredRegister.id(path));
        return ITEMS.register(path, () -> factory.apply(new Item.Properties().setId(key)));
    }

    /**
     * Registers a {@link BlockItem} so a block can be held and placed from inventory.
     *
     * <p>The {@code path} must match the block's registered path exactly, otherwise
     * the item will have a different id to the block.
     *
     * @param path  must match the block's registered path exactly
     * @param block the block this item represents
     * @return the registered BlockItem entry
     */
    protected static RegistryEntry<BlockItem> registerBlockItem(String path, RegistryEntry<Block> block) {
        ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), DeferredRegister.id(path));
        return ITEMS.register(path, () -> {
            BlockItem item = new BlockItem(block.get(), new Item.Properties()
                    .setId(key)
                    .useBlockDescriptionPrefix());
            item.registerBlocks(Item.BY_BLOCK, item);
            return item;
        });
    }

    /** Like {@link #registerBlockItem(String, RegistryEntry)}, with a custom max stack size. */
    protected static RegistryEntry<BlockItem> registerBlockItemWithCustomStackSize(String path, RegistryEntry<Block> block, int stackSize) {
        ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), DeferredRegister.id(path));
        return ITEMS.register(path, () -> {
            BlockItem item = new BlockItem(block.get(), new Item.Properties()
                    .setId(key)
                    .useBlockDescriptionPrefix()
                    .stacksTo(stackSize));
            item.registerBlocks(Item.BY_BLOCK, item);
            return item;
        });
    }
}
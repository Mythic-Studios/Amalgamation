package org.mythic_goose.amalgamation.api.registry_v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

/** Registry helper for items. Each mod that extends this class gets its own register. */
public abstract class ItemRegistry {

    private static DeferredRegister<Item> items() {
        return DeferredRegister.forCallingMod(BuiltInRegistries.ITEM);
    }

    protected static RegistryEntry<Item> registerItem(String path, Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), DeferredRegister.id(path));
        return items().register(path, () -> factory.apply(new Item.Properties().setId(key)));
    }

    protected static RegistryEntry<BlockItem> registerBlockItem(String path, RegistryEntry<Block> block) {
        ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), DeferredRegister.id(path));
        return items().register(path, () -> {
            BlockItem item = new BlockItem(block.get(), new Item.Properties()
                    .setId(key)
                    .useBlockDescriptionPrefix());
            item.registerBlocks(Item.BY_BLOCK, item);
            return item;
        });
    }

    protected static RegistryEntry<BlockItem> registerBlockItemWithCustomStackSize(String path, RegistryEntry<Block> block, int stackSize) {
        ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), DeferredRegister.id(path));
        return items().register(path, () -> {
            BlockItem item = new BlockItem(block.get(), new Item.Properties()
                    .setId(key)
                    .useBlockDescriptionPrefix()
                    .stacksTo(stackSize));
            item.registerBlocks(Item.BY_BLOCK, item);
            return item;
        });
    }
}
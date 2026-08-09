package org.mythic_goose.amalgamation.library.datagen_helpers_v1;

import net.minecraft.client.color.item.Dye;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.data.models.ItemModelGenerators.TRIM_MATERIAL_MODELS;

/**
 * This is going to make ModelGen look so much neater
 */
public class ModelDatagenHelper {

    /**
     * @param itemModelGenerators Input the datagen provider to use it (Will not work without it)
     * @param item The item you wish to use
     * <p>
     *             ‎
     * <p>
     * This generates the model for gui, chests, and shelfs whereas if you provide a model with the suffix "in_hand.json"
     *             it shows the item in 3d in your hand and for other players.
     * <p>
     *             ‎
     * <p>
     * This is a custom method and there is no vanilla alternative
     */
    public final void generate3DItem(@NonNull ItemModelGenerators itemModelGenerators, final Item item) {
        ItemModel.Unbaked flatModel = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, ModelTemplates.FLAT_ITEM));

        Identifier inHandLocation = ModelLocationUtils.getModelLocation(item, "_in_hand");
        ItemModel.Unbaked inHandRef = ItemModelUtils.plainModel(inHandLocation);

        itemModelGenerators.itemModelOutput.accept(item, ItemModelGenerators.createFlatModelDispatch(flatModel, inHandRef), new ClientItem.Properties(true, false, 1.95F));
    }

    /**
     * @param itemModelGenerators Input the datagen provider to use it (Will not work without it)
     * @param item The item you wish to use
     * <p>
     *             ‎
     * <p>
     * This generates the model as a flat item (e.g. Diamond, Emerald, Iron Ingot, ect.)
     */
    public final void simpleItem(@NonNull ItemModelGenerators itemModelGenerators, final Item item) {
        itemModelGenerators.createFlatItemModel(item, ModelTemplates.FLAT_ITEM);
    }

    /**
     * @param itemModelGenerators Input the datagen provider to use it (Will not work without it)
     * @param item The item you wish to use
     * <p>
     *             ‎
     * <p>
     * This generates the model as an item you hold in your hand (e.g. Pickaxes, Axes and Swords)
     */
    public final void holdingItem(@NonNull ItemModelGenerators itemModelGenerators, final Item item) {
        itemModelGenerators.createFlatItemModel(item, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    /**
     * @param itemModelGenerators Input the datagen provider to use it (Will not work without it)
     * @param armor The Helmet item that you registered
     * @param equipmentAssetId The Material Key registered
     * @param hasDyedLayer Used for like Leather armor - (Not really needed)
     */
    public final void trimmableHelmet(@NonNull ItemModelGenerators itemModelGenerators, final Item armor, final ResourceKey<EquipmentAsset> equipmentAssetId, final boolean hasDyedLayer) {
        Identifier modelLocation = ModelLocationUtils.getModelLocation(armor);
        Material itemTexture = TextureMapping.getItemTexture(armor);
        Material overlayTexture = TextureMapping.getItemTexture(armor, "_overlay");
        List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList(TRIM_MATERIAL_MODELS.size());

        for(ItemModelGenerators.TrimMaterialData material : TRIM_MATERIAL_MODELS) {
            Identifier trimModelLocation = modelLocation.withSuffix("_" + material.assets().base().suffix() + "_trim");
            String var10003 = material.assets().assetId(equipmentAssetId).suffix();
            Material trimOverlayTexture = new Material(ItemModelGenerators.TRIM_PREFIX_HELMET.withSuffix("_" + var10003));
            ItemModel.Unbaked trimModel;
            if (hasDyedLayer) {
                itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, overlayTexture, trimOverlayTexture);
                trimModel = ItemModelUtils.tintedModel(trimModelLocation, new ItemTintSource[]{new Dye(-6265536)});
            } else {
                itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, trimOverlayTexture);
                trimModel = ItemModelUtils.plainModel(trimModelLocation);
            }

            cases.add(ItemModelUtils.when(material.materialKey(), trimModel));
        }

        ItemModel.Unbaked untrimmedModel;
        if (hasDyedLayer) {
            ModelTemplates.TWO_LAYERED_ITEM.create(modelLocation, TextureMapping.layered(itemTexture, overlayTexture), itemModelGenerators.modelOutput);
            untrimmedModel = ItemModelUtils.tintedModel(modelLocation, new ItemTintSource[]{new Dye(-6265536)});
        } else {
            ModelTemplates.FLAT_ITEM.create(modelLocation, TextureMapping.layer0(itemTexture), itemModelGenerators.modelOutput);
            untrimmedModel = ItemModelUtils.plainModel(modelLocation);
        }

        itemModelGenerators.itemModelOutput.accept(armor, ItemModelUtils.select(new TrimMaterialProperty(), untrimmedModel, cases));
    }

    /**
     * @param itemModelGenerators Input the datagen provider to use it (Will not work without it)
     * @param armor The Chestplate item that you registered
     * @param equipmentAssetId The Material Key registered
     * @param hasDyedLayer Used for like Leather armor - (Not really needed)
     */
    public final void trimmableChestplate(@NonNull ItemModelGenerators itemModelGenerators, final Item armor, final ResourceKey<EquipmentAsset> equipmentAssetId, final boolean hasDyedLayer) {
        Identifier modelLocation = ModelLocationUtils.getModelLocation(armor);
        Material itemTexture = TextureMapping.getItemTexture(armor);
        Material overlayTexture = TextureMapping.getItemTexture(armor, "_overlay");
        List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList(TRIM_MATERIAL_MODELS.size());

        for(ItemModelGenerators.TrimMaterialData material : TRIM_MATERIAL_MODELS) {
            Identifier trimModelLocation = modelLocation.withSuffix("_" + material.assets().base().suffix() + "_trim");
            String var10003 = material.assets().assetId(equipmentAssetId).suffix();
            Material trimOverlayTexture = new Material(ItemModelGenerators.TRIM_PREFIX_CHESTPLATE.withSuffix("_" + var10003));
            ItemModel.Unbaked trimModel;
            if (hasDyedLayer) {
                itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, overlayTexture, trimOverlayTexture);
                trimModel = ItemModelUtils.tintedModel(trimModelLocation, new ItemTintSource[]{new Dye(-6265536)});
            } else {
                itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, trimOverlayTexture);
                trimModel = ItemModelUtils.plainModel(trimModelLocation);
            }

            cases.add(ItemModelUtils.when(material.materialKey(), trimModel));
        }

        ItemModel.Unbaked untrimmedModel;
        if (hasDyedLayer) {
            ModelTemplates.TWO_LAYERED_ITEM.create(modelLocation, TextureMapping.layered(itemTexture, overlayTexture), itemModelGenerators.modelOutput);
            untrimmedModel = ItemModelUtils.tintedModel(modelLocation, new ItemTintSource[]{new Dye(-6265536)});
        } else {
            ModelTemplates.FLAT_ITEM.create(modelLocation, TextureMapping.layer0(itemTexture), itemModelGenerators.modelOutput);
            untrimmedModel = ItemModelUtils.plainModel(modelLocation);
        }

        itemModelGenerators.itemModelOutput.accept(armor, ItemModelUtils.select(new TrimMaterialProperty(), untrimmedModel, cases));
    }

    /**
     * @param itemModelGenerators Input the datagen provider to use it (Will not work without it)
     * @param armor The Leggings item that you registered
     * @param equipmentAssetId The Material Key registered
     * @param hasDyedLayer Used for like Leather armor - (Not really needed)
     */
    public final void trimmableLeggings(@NonNull ItemModelGenerators itemModelGenerators, final Item armor, final ResourceKey<EquipmentAsset> equipmentAssetId, final boolean hasDyedLayer) {
        Identifier modelLocation = ModelLocationUtils.getModelLocation(armor);
        Material itemTexture = TextureMapping.getItemTexture(armor);
        Material overlayTexture = TextureMapping.getItemTexture(armor, "_overlay");
        List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList(TRIM_MATERIAL_MODELS.size());

        for(ItemModelGenerators.TrimMaterialData material : TRIM_MATERIAL_MODELS) {
            Identifier trimModelLocation = modelLocation.withSuffix("_" + material.assets().base().suffix() + "_trim");
            String var10003 = material.assets().assetId(equipmentAssetId).suffix();
            Material trimOverlayTexture = new Material(ItemModelGenerators.TRIM_PREFIX_LEGGINGS.withSuffix("_" + var10003));
            ItemModel.Unbaked trimModel;
            if (hasDyedLayer) {
                itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, overlayTexture, trimOverlayTexture);
                trimModel = ItemModelUtils.tintedModel(trimModelLocation, new ItemTintSource[]{new Dye(-6265536)});
            } else {
                itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, trimOverlayTexture);
                trimModel = ItemModelUtils.plainModel(trimModelLocation);
            }

            cases.add(ItemModelUtils.when(material.materialKey(), trimModel));
        }

        ItemModel.Unbaked untrimmedModel;
        if (hasDyedLayer) {
            ModelTemplates.TWO_LAYERED_ITEM.create(modelLocation, TextureMapping.layered(itemTexture, overlayTexture), itemModelGenerators.modelOutput);
            untrimmedModel = ItemModelUtils.tintedModel(modelLocation, new ItemTintSource[]{new Dye(-6265536)});
        } else {
            ModelTemplates.FLAT_ITEM.create(modelLocation, TextureMapping.layer0(itemTexture), itemModelGenerators.modelOutput);
            untrimmedModel = ItemModelUtils.plainModel(modelLocation);
        }

        itemModelGenerators.itemModelOutput.accept(armor, ItemModelUtils.select(new TrimMaterialProperty(), untrimmedModel, cases));
    }

    /**
     * @param itemModelGenerators Input the datagen provider to use it (Will not work without it)
     * @param armor The Boots item that you registered
     * @param equipmentAssetId The Material Key registered
     * @param hasDyedLayer Used for like Leather armor - (Not really needed)
     */
    public final void trimmableBoots(@NonNull ItemModelGenerators itemModelGenerators, final Item armor, final ResourceKey<EquipmentAsset> equipmentAssetId, final boolean hasDyedLayer) {
        Identifier modelLocation = ModelLocationUtils.getModelLocation(armor);
        Material itemTexture = TextureMapping.getItemTexture(armor);
        Material overlayTexture = TextureMapping.getItemTexture(armor, "_overlay");
        List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList(TRIM_MATERIAL_MODELS.size());

        for(ItemModelGenerators.TrimMaterialData material : TRIM_MATERIAL_MODELS) {
            Identifier trimModelLocation = modelLocation.withSuffix("_" + material.assets().base().suffix() + "_trim");
            String var10003 = material.assets().assetId(equipmentAssetId).suffix();
            Material trimOverlayTexture = new Material(ItemModelGenerators.TRIM_PREFIX_BOOTS.withSuffix("_" + var10003));
            ItemModel.Unbaked trimModel;
            if (hasDyedLayer) {
                itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, overlayTexture, trimOverlayTexture);
                trimModel = ItemModelUtils.tintedModel(trimModelLocation, new ItemTintSource[]{new Dye(-6265536)});
            } else {
                itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, trimOverlayTexture);
                trimModel = ItemModelUtils.plainModel(trimModelLocation);
            }

            cases.add(ItemModelUtils.when(material.materialKey(), trimModel));
        }

        ItemModel.Unbaked untrimmedModel;
        if (hasDyedLayer) {
            ModelTemplates.TWO_LAYERED_ITEM.create(modelLocation, TextureMapping.layered(itemTexture, overlayTexture), itemModelGenerators.modelOutput);
            untrimmedModel = ItemModelUtils.tintedModel(modelLocation, new ItemTintSource[]{new Dye(-6265536)});
        } else {
            ModelTemplates.FLAT_ITEM.create(modelLocation, TextureMapping.layer0(itemTexture), itemModelGenerators.modelOutput);
            untrimmedModel = ItemModelUtils.plainModel(modelLocation);
        }

        itemModelGenerators.itemModelOutput.accept(armor, ItemModelUtils.select(new TrimMaterialProperty(), untrimmedModel, cases));
    }

    /**
     * @param blockModelGenerators Input the datagen provider to use it (Will not work without it)
     * @param block The Block you wish to use
     */
    public final void sameSidesBlock(@NonNull BlockModelGenerators blockModelGenerators, final Block block) {
        blockModelGenerators.createTrivialBlock(block, TexturedModel.CUBE);
    }
}

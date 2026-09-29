package org.mythic_goose.amalgamation.api.datagen_helpers_v1;

import net.minecraft.client.color.item.Dye;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;
import org.mythic_goose.amalgamation.api.component_v1.ComponentIntProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.minecraft.client.data.models.ItemModelGenerators.TRIM_MATERIAL_MODELS;

/**
 * Adds new Methods to Datagen
 */
public class DatagenHelpers {

    public static class ItemModels {
        /**
         * @param itemModelGenerators Input the datagen provider to use it (Will not work without it)
         * @param item The item you wish to use
         * <p>
         *             ‎
         * <p>
         * This generates the model for gui, chests, and shelfs whereas if you provide a model with the suffix "_in_hand.json"
         *             it shows the item in 3d in your hand and for other players.
         * <p>
         *             ‎
         * <p>
         * This is a custom method and there is no vanilla alternative
         */
        public static void generate3DItem(@NonNull ItemModelGenerators itemModelGenerators, final Item item) {
            net.minecraft.client.renderer.item.ItemModel.Unbaked flatModel = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, ModelTemplates.FLAT_ITEM));

            Identifier inHandLocation = ModelLocationUtils.getModelLocation(item, "_in_hand");
            net.minecraft.client.renderer.item.ItemModel.Unbaked inHandRef = ItemModelUtils.plainModel(inHandLocation);

            itemModelGenerators.itemModelOutput.accept(item, ItemModelGenerators.createFlatModelDispatch(flatModel, inHandRef), new ClientItem.Properties(true, false, 1.95F));
        }

        /**
         * @param item The Item you want to use
         * @param itemModelOutput Input the datagen output provider to use it
         * @param itemModels Input the datagen provider to use it
         */
        public static void generateBow(Item item, ItemModelOutput itemModelOutput, ItemModelGenerators itemModels) {
            net.minecraft.client.renderer.item.ItemModel.Unbaked bowModel = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item));
            net.minecraft.client.renderer.item.ItemModel.Unbaked pulling0 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_pulling_0", ModelTemplates.BOW));
            net.minecraft.client.renderer.item.ItemModel.Unbaked pulling1 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_pulling_1", ModelTemplates.BOW));
            net.minecraft.client.renderer.item.ItemModel.Unbaked pulling2 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_pulling_2", ModelTemplates.BOW));
            itemModels.createFlatItemModel(item, ModelTemplates.BOW);
            itemModelOutput.accept(item, ItemModelUtils.conditional(
                    ItemModelUtils.isUsingItem(),
                    ItemModelUtils.rangeSelect(new UseDuration(false), 0.05F, pulling0, new RangeSelectItemModel.Entry[]{
                            ItemModelUtils.override(pulling1, 0.65F),
                            ItemModelUtils.override(pulling2, 0.9F)
                    }),
                    bowModel
            ));
        }

        /**
         * @param itemModelGenerators Input the datagen provider to use it
         * @param item The Item you wish to use
         * @param suffix The Difference in texture name
         * @param component The Data Component the item uses to operate (Must be a numbers one)
         * @param defaultStartingNumber The number the item starts at normally
         * @param threshold The number for it to reach (at or above, unless mustEqual == true) for it to change model
         * @param mustEqual Set to make it so it has to equal the threshold to work
         */
        public static void generateIntThresholdItem(ItemModelGenerators itemModelGenerators, Item item, String suffix,
                                             DataComponentType<Integer> component, int defaultStartingNumber, int threshold,
                                             boolean mustEqual) {
            net.minecraft.client.renderer.item.ItemModel.Unbaked unbakedDefault = ItemModelUtils.plainModel(
                    itemModelGenerators.createFlatItemModel(item, ModelTemplates.FLAT_ITEM));
            net.minecraft.client.renderer.item.ItemModel.Unbaked unbakedMatched = ItemModelUtils.plainModel(
                    itemModelGenerators.createFlatItemModel(item, suffix, ModelTemplates.FLAT_ITEM));

            List<RangeSelectItemModel.Entry> entries = new ArrayList<>();
            entries.add(new RangeSelectItemModel.Entry(threshold, unbakedMatched));

            if (mustEqual) {
                // Immediately revert to default the moment the value passes the threshold,
                // so only an exact match at `threshold` shows unbakedMatched.
                entries.add(new RangeSelectItemModel.Entry(threshold + 1, unbakedDefault));
            }
            // if !mustEqual, entries only has the lower bound -> "at or above threshold" behavior

            itemModelGenerators.itemModelOutput.accept(item,
                    new ClientItem(new RangeSelectItemModel.Unbaked(
                            Optional.empty(),
                            new ComponentIntProperty(component, defaultStartingNumber),
                            1.0f,
                            entries,
                            Optional.of(unbakedDefault)), // fallback covers "below threshold"
                            new ClientItem.Properties(false, false, 1f)).model());
        }

        /**
         * @param itemModelGenerators Input the datagen provider to use it (Will not work without it)
         * @param armor The Helmet item that you registered
         * @param equipmentAssetId The Material Key registered
         * @param hasDyedLayer Used for like Leather armor - (Not really needed)
         */
        public static void trimmableHelmet(@NonNull ItemModelGenerators itemModelGenerators, final Item armor, final ResourceKey<EquipmentAsset> equipmentAssetId, final boolean hasDyedLayer) {
            Identifier modelLocation = ModelLocationUtils.getModelLocation(armor);
            Material itemTexture = TextureMapping.getItemTexture(armor);
            Material overlayTexture = TextureMapping.getItemTexture(armor, "_overlay");
            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList(TRIM_MATERIAL_MODELS.size());

            for(ItemModelGenerators.TrimMaterialData material : TRIM_MATERIAL_MODELS) {
                Identifier trimModelLocation = modelLocation.withSuffix("_" + material.assets().base().suffix() + "_trim");
                String var10003 = material.assets().assetId(equipmentAssetId).suffix();
                Material trimOverlayTexture = new Material(ItemModelGenerators.TRIM_PREFIX_HELMET.withSuffix("_" + var10003));
                net.minecraft.client.renderer.item.ItemModel.Unbaked trimModel;
                if (hasDyedLayer) {
                    itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, overlayTexture, trimOverlayTexture);
                    trimModel = ItemModelUtils.tintedModel(trimModelLocation, new ItemTintSource[]{new Dye(-6265536)});
                } else {
                    itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, trimOverlayTexture);
                    trimModel = ItemModelUtils.plainModel(trimModelLocation);
                }

                cases.add(ItemModelUtils.when(material.materialKey(), trimModel));
            }

            net.minecraft.client.renderer.item.ItemModel.Unbaked untrimmedModel;
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
        public static void trimmableChestplate(@NonNull ItemModelGenerators itemModelGenerators, final Item armor, final ResourceKey<EquipmentAsset> equipmentAssetId, final boolean hasDyedLayer) {
            Identifier modelLocation = ModelLocationUtils.getModelLocation(armor);
            Material itemTexture = TextureMapping.getItemTexture(armor);
            Material overlayTexture = TextureMapping.getItemTexture(armor, "_overlay");
            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList(TRIM_MATERIAL_MODELS.size());

            for(ItemModelGenerators.TrimMaterialData material : TRIM_MATERIAL_MODELS) {
                Identifier trimModelLocation = modelLocation.withSuffix("_" + material.assets().base().suffix() + "_trim");
                String var10003 = material.assets().assetId(equipmentAssetId).suffix();
                Material trimOverlayTexture = new Material(ItemModelGenerators.TRIM_PREFIX_CHESTPLATE.withSuffix("_" + var10003));
                net.minecraft.client.renderer.item.ItemModel.Unbaked trimModel;
                if (hasDyedLayer) {
                    itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, overlayTexture, trimOverlayTexture);
                    trimModel = ItemModelUtils.tintedModel(trimModelLocation, new ItemTintSource[]{new Dye(-6265536)});
                } else {
                    itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, trimOverlayTexture);
                    trimModel = ItemModelUtils.plainModel(trimModelLocation);
                }

                cases.add(ItemModelUtils.when(material.materialKey(), trimModel));
            }

            net.minecraft.client.renderer.item.ItemModel.Unbaked untrimmedModel;
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
        public static void trimmableLeggings(@NonNull ItemModelGenerators itemModelGenerators, final Item armor, final ResourceKey<EquipmentAsset> equipmentAssetId, final boolean hasDyedLayer) {
            Identifier modelLocation = ModelLocationUtils.getModelLocation(armor);
            Material itemTexture = TextureMapping.getItemTexture(armor);
            Material overlayTexture = TextureMapping.getItemTexture(armor, "_overlay");
            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList(TRIM_MATERIAL_MODELS.size());

            for(ItemModelGenerators.TrimMaterialData material : TRIM_MATERIAL_MODELS) {
                Identifier trimModelLocation = modelLocation.withSuffix("_" + material.assets().base().suffix() + "_trim");
                String var10003 = material.assets().assetId(equipmentAssetId).suffix();
                Material trimOverlayTexture = new Material(ItemModelGenerators.TRIM_PREFIX_LEGGINGS.withSuffix("_" + var10003));
                net.minecraft.client.renderer.item.ItemModel.Unbaked trimModel;
                if (hasDyedLayer) {
                    itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, overlayTexture, trimOverlayTexture);
                    trimModel = ItemModelUtils.tintedModel(trimModelLocation, new ItemTintSource[]{new Dye(-6265536)});
                } else {
                    itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, trimOverlayTexture);
                    trimModel = ItemModelUtils.plainModel(trimModelLocation);
                }

                cases.add(ItemModelUtils.when(material.materialKey(), trimModel));
            }

            net.minecraft.client.renderer.item.ItemModel.Unbaked untrimmedModel;
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
        public static void trimmableBoots(@NonNull ItemModelGenerators itemModelGenerators, final Item armor, final ResourceKey<EquipmentAsset> equipmentAssetId, final boolean hasDyedLayer) {
            Identifier modelLocation = ModelLocationUtils.getModelLocation(armor);
            Material itemTexture = TextureMapping.getItemTexture(armor);
            Material overlayTexture = TextureMapping.getItemTexture(armor, "_overlay");
            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList(TRIM_MATERIAL_MODELS.size());

            for(ItemModelGenerators.TrimMaterialData material : TRIM_MATERIAL_MODELS) {
                Identifier trimModelLocation = modelLocation.withSuffix("_" + material.assets().base().suffix() + "_trim");
                String var10003 = material.assets().assetId(equipmentAssetId).suffix();
                Material trimOverlayTexture = new Material(ItemModelGenerators.TRIM_PREFIX_BOOTS.withSuffix("_" + var10003));
                net.minecraft.client.renderer.item.ItemModel.Unbaked trimModel;
                if (hasDyedLayer) {
                    itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, overlayTexture, trimOverlayTexture);
                    trimModel = ItemModelUtils.tintedModel(trimModelLocation, new ItemTintSource[]{new Dye(-6265536)});
                } else {
                    itemModelGenerators.generateLayeredItem(trimModelLocation, itemTexture, trimOverlayTexture);
                    trimModel = ItemModelUtils.plainModel(trimModelLocation);
                }

                cases.add(ItemModelUtils.when(material.materialKey(), trimModel));
            }

            net.minecraft.client.renderer.item.ItemModel.Unbaked untrimmedModel;
            if (hasDyedLayer) {
                ModelTemplates.TWO_LAYERED_ITEM.create(modelLocation, TextureMapping.layered(itemTexture, overlayTexture), itemModelGenerators.modelOutput);
                untrimmedModel = ItemModelUtils.tintedModel(modelLocation, new ItemTintSource[]{new Dye(-6265536)});
            } else {
                ModelTemplates.FLAT_ITEM.create(modelLocation, TextureMapping.layer0(itemTexture), itemModelGenerators.modelOutput);
                untrimmedModel = ItemModelUtils.plainModel(modelLocation);
            }

            itemModelGenerators.itemModelOutput.accept(armor, ItemModelUtils.select(new TrimMaterialProperty(), untrimmedModel, cases));
        }
    }
}

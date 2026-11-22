package org.mythic_goose.amalgamation.testmod;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.mythic_goose.amalgamation.api.tooltip_v2.panel.CustomPanel;
import org.mythic_goose.amalgamation.api.tooltip_v2.panel.PanelDesign;
import org.mythic_goose.amalgamation.api.tooltip_v2.panel.TagLikeLayout;

import java.util.function.Consumer;

public class CustomItemHover extends Item implements CustomPanel {
    public CustomItemHover(Properties properties) {
        super(properties);
    }

    @Override
    public TagLikeLayout getLayout(ItemStack stack) {
        return TagLikeLayout.tagLike(Component.literal("Rarity: ~dSecret"))
                .description(Component.literal("Testing Lore. This item is special and uses advanced techniques, to burn or poison. ECT EXT"))
                .modName("Test Mod")
                .panel(PanelDesign.animatedBorder(0xF0200800, 0xFFFF4400, 0xFFFFCC00, 2000));
    }
}

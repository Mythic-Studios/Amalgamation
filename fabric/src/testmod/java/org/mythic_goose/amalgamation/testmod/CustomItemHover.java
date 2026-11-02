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
        return TagLikeLayout.tagLike(Component.literal("Rarity: ~dSecret"))   // subtext tag (a Component)
                .description(Component.literal("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                        "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus." +
                        "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci." +
                        "Aenean nec lorem. In porttitor. Donec laoreet nonummy augue." +
                        "Suspendisse dui purus, scelerisque at, vulputate vitae, pretium mattis, nunc. Mauris eget neque at sem venenatis eleifend. Ut nonummy."))  // description box
                .modName("Test Mod")                                             // optional
                .panel(PanelDesign.animatedBorder(0xF0200800, 0xFFFF4400, 0xFFFFCC00, 2000)); // optional box colours
    }
}

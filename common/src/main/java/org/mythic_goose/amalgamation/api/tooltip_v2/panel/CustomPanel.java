package org.mythic_goose.amalgamation.api.tooltip_v2.panel;

import net.minecraft.world.item.ItemStack;

/**
 * Implement this on an Item to customise its tooltip box.
 *
 * <pre>{@code
 * public class FireBlade extends SwordItem implements CustomPanel {
 *     // Simple: just recolour the box
 *     public PanelDesign getPanelDesign(ItemStack stack) {
 *         return PanelDesign.animatedBorder(0xF0200800, 0xFFFF4400, 0xFFFFCC00, 2000);
 *     }
 *
 *     // Optional: card layout with icon / name / tag / description / mod name
 *     public TagLikeLayout getLayout(ItemStack stack) {
 *         return TagLikeLayout.tagLike(Component.literal("Legendary Sword"))
 *                 .description(Component.literal("Burns whatever it touches."))
 *                 .modName("MY MOD");
 *     }
 * }
 * }</pre>
 */
public interface CustomPanel {
    /** Box colours. Return null for vanilla. */
    default PanelDesign getPanelDesign(ItemStack stack) {
        TagLikeLayout l = getLayout(stack);
        return l != null ? l.design() : null;
    }

    /** Card layout. Return null to keep the normal line-based tooltip. */
    default TagLikeLayout getLayout(ItemStack stack) {
        return null;
    }
}
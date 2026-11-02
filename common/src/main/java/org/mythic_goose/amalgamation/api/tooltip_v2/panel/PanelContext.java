package org.mythic_goose.amalgamation.api.tooltip_v2.panel;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.mythic_goose.amalgamation.api.tooltip_v2.ItemTooltips;

import java.util.List;

/**
 * Remembers what the tooltip currently being built wants. The tooltip callback fires
 * right before the tooltip is drawn, so we capture the stack's design/layout there.
 */
public final class PanelContext {
    private static PanelDesign design;
    private static TagPanelComponent card;
    private static boolean initialised;

    private PanelContext() {}

    /** Call once from client init. */
    public static void init() {
        if (initialised) return;
        initialised = true;
        ItemTooltips.register((stack, context, flags, lines) -> {
            design = null;
            card = null;
            if (stack.getItem() instanceof CustomPanel panel) {
                design = panel.getPanelDesign(stack);
                TagLikeLayout layout = panel.getLayout(stack);
                if (layout != null) card = new TagPanelComponent(stack, layout);
            }
        });
    }

    /** Background renderer: returns the pending design (or null) and clears it. */
    public static PanelDesign consumeDesign() {
        PanelDesign d = design;
        design = null;
        return d;
    }

    /**
     * Call where your tooltip component list is built (before TooltipHelper.newFix).
     * If the hovered item has a card layout, the whole list is replaced by the card.
     */
    public static void applyCard(List<ClientTooltipComponent> components) {
        if (card == null) return;
        components.clear();
        components.add(card);
        card = null;
    }
}
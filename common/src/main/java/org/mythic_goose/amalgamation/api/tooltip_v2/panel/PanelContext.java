package org.mythic_goose.amalgamation.api.tooltip_v2.panel;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.mythic_goose.amalgamation.AmalgamationConstants;
import org.mythic_goose.amalgamation.api.tooltip_v2.ItemTooltips;

import java.util.List;

/**
 * Remembers what the tooltip currently being built wants. The tooltip callback fires
 * right before the tooltip is drawn, so we capture the stack's design/layout there.
 */
public final class PanelContext {
    /** Pending state older than this is considered stale and ignored (the tooltip is drawn the same/next frame). */
    private static final long MAX_AGE_NANOS = 250_000_000L;

    private static PanelDesign design;
    private static TagPanelComponent card;
    private static long capturedAt;
    private static boolean initialised;
    private static boolean loggedFailure;

    private PanelContext() {}

    /** Call once from client init. */
    public static void init() {
        if (initialised) return;
        initialised = true;
        ItemTooltips.register((stack, context, flags, lines) -> capture(stack));
    }

    private static void capture(ItemStack stack) {
        design = null;
        card = null;

        if (!(stack.getItem() instanceof CustomPanel panel)) return;

        try {
            // Evaluate the layout once: getLayout() may read stack data and must not be called twice
            // with a chance of different results.
            TagLikeLayout layout = panel.getLayout(stack);
            PanelDesign d = layout != null ? layout.design() : panel.getPanelDesign(stack);
            TagPanelComponent c = layout != null ? new TagPanelComponent(stack, layout) : null;

            // Only publish once everything was built, so a failure can never leave a design without its card.
            design = d;
            card = c;
            capturedAt = System.nanoTime();
        } catch (Throwable t) {
            design = null;
            card = null;
            if (!loggedFailure) {
                loggedFailure = true;
                AmalgamationConstants.LOG.error(
                        "tooltip_v2: failed to build the panel for {}; falling back to the vanilla tooltip "
                                + "(further failures are not logged)", stack.getItem(), t);
            }
        }
    }

    private static boolean stale() {
        return System.nanoTime() - capturedAt > MAX_AGE_NANOS;
    }

    /** Background renderer: returns the pending design (or null) and clears it. */
    public static PanelDesign consumeDesign() {
        PanelDesign d = stale() ? null : design;
        design = null;
        return d;
    }

    /**
     * Call where your tooltip component list is built (before TooltipHelper.newFix).
     * If the hovered item has a card layout, the whole list is replaced by the card.
     */
    public static void applyCard(List<ClientTooltipComponent> components) {
        TagPanelComponent c = stale() ? null : card;
        card = null;
        if (c == null) return;
        components.clear();
        components.add(c);
    }
}
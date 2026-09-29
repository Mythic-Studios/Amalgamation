package org.mythic_goose.amalgamation.api.tooltip_v1.wrapping;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.util.List;

/**
 * A tooltip component that can split itself into narrower components
 * so it fits within the available tooltip width.
 */
public interface WrappableTooltipComponent extends ClientTooltipComponent {

    /**
     * @return one or more components, each no wider than maxWidth.
     *         Return List.of(this) if no wrapping is needed.
     */
    List<ClientTooltipComponent> wrapTo(Font font, int maxWidth);
}
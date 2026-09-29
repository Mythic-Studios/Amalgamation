package org.mythic_goose.amalgamation.api.tooltip_v1;

import org.mythic_goose.amalgamation.api.tooltip_v1.callback.ItemTooltipCallback;
import org.mythic_goose.amalgamation.api.tooltip_v1.callback.ItemTooltipPlatform;

public final class FabricItemTooltipPlatform implements ItemTooltipPlatform {
    @Override
    public void register(ItemTooltipCallback callback) {
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register(callback::onTooltip);
    }
}
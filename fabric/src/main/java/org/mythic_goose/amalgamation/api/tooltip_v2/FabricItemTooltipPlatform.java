package org.mythic_goose.amalgamation.api.tooltip_v2;

import org.mythic_goose.amalgamation.api.tooltip_v2.callback.ItemTooltipCallback;
import org.mythic_goose.amalgamation.api.tooltip_v2.callback.ItemTooltipPlatform;

public final class FabricItemTooltipPlatform implements ItemTooltipPlatform {
    @Override
    public void register(ItemTooltipCallback callback) {
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register(callback::onTooltip);
    }
}
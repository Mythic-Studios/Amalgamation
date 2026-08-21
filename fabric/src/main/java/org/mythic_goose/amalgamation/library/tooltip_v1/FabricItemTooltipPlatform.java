package org.mythic_goose.amalgamation.library.tooltip_v1;

public final class FabricItemTooltipPlatform implements ItemTooltipPlatform {
    @Override
    public void register(ItemTooltipCallback callback) {
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register(callback::onTooltip);
    }
}
package org.mythic_goose.amalgamation.platform;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import org.mythic_goose.amalgamation.platform.services.IItemTooltipHelper;

public class FabricItemTooltipHelper implements IItemTooltipHelper {
    @Override
    public void register(org.mythic_goose.amalgamation.library.tooltip_v1.ItemTooltipCallback callback) {
        ItemTooltipCallback.EVENT.register(callback::onTooltip);
    }
}
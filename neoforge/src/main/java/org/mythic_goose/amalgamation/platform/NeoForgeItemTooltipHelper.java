package org.mythic_goose.amalgamation.platform;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.mythic_goose.amalgamation.library.tooltip_v1.ItemTooltipCallback;
import org.mythic_goose.amalgamation.platform.services.IItemTooltipHelper;

public class NeoForgeItemTooltipHelper implements IItemTooltipHelper {
    @Override
    public void register(ItemTooltipCallback callback) {
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent event) ->
                callback.onTooltip(event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip()));
    }
}
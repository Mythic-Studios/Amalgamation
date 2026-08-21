package org.mythic_goose.amalgamation.library.tooltip_v1;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class NeoForgeItemTooltipPlatform implements ItemTooltipPlatform {
    @Override
    public void register(ItemTooltipCallback callback) {
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent event) ->
                callback.onTooltip(event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip()));
    }
}
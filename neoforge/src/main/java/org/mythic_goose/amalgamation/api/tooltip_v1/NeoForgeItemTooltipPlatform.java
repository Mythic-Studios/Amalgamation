package org.mythic_goose.amalgamation.api.tooltip_v1;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.mythic_goose.amalgamation.api.tooltip_v1.callback.ItemTooltipCallback;
import org.mythic_goose.amalgamation.api.tooltip_v1.callback.ItemTooltipPlatform;

public final class NeoForgeItemTooltipPlatform implements ItemTooltipPlatform {
    @Override
    public void register(ItemTooltipCallback callback) {
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent event) ->
                callback.onTooltip(event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip()));
    }
}
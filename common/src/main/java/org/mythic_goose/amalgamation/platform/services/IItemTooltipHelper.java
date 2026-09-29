package org.mythic_goose.amalgamation.platform.services;


import org.mythic_goose.amalgamation.api.tooltip_v1.callback.ItemTooltipCallback;

public interface IItemTooltipHelper {
    void register(ItemTooltipCallback callback);
}
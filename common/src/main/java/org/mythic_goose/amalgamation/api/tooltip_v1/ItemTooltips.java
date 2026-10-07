package org.mythic_goose.amalgamation.api.tooltip_v1;

import org.mythic_goose.amalgamation.api.tooltip_v1.callback.ItemTooltipCallback;
import org.mythic_goose.amalgamation.api.tooltip_v1.callback.ItemTooltipPlatform;

import java.util.ServiceLoader;

public final class ItemTooltips {
    private static final ItemTooltipPlatform PLATFORM = ServiceLoader
            .load(ItemTooltipPlatform.class, ItemTooltipPlatform.class.getClassLoader())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No ItemTooltipPlatform implementation found"));

    private ItemTooltips() {}

    public static void register(ItemTooltipCallback callback) {
        PLATFORM.register(callback);
    }
}
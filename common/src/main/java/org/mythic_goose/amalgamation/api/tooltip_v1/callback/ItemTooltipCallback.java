package org.mythic_goose.amalgamation.api.tooltip_v1.callback;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/** Loader-agnostic replacement for Fabric's ItemTooltipCallback. */
@FunctionalInterface
public interface ItemTooltipCallback {
    void onTooltip(ItemStack stack, Item.TooltipContext context, TooltipFlag flags, List<Component> lines);
}
package org.mythic_goose.amalgamation.mixin.accessor;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
public interface ItemPickerMenuAccessor {

    @Invoker("getRowIndexForScroll")
    int invokeGetRowIndexForScroll(float scrollOffs);
}
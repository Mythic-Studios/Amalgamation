package org.mythic_goose.amalgamation.mixin.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface CreativeModeInventoryScreenAccessor {
    @Accessor("leftPos")
    int amalgamation$getLeftPos();

    @Accessor("topPos")
    int amalgamation$getTopPos();
}
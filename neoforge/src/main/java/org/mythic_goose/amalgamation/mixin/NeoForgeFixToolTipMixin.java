package org.mythic_goose.amalgamation.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * NeoForge patches {@code GuiGraphicsExtractor} so that deferred item tooltips call a new overload,
 * {@code tooltip(Font, List, int, int, ClientTooltipPositioner, Identifier, ItemStack)}, which contains the
 * real drawing code. The vanilla 6-argument {@code tooltip(...)} that {@link FixToolTipMixin} hooks is only a
 * thin delegate there and is never reached for item tooltips, so the card was never applied (only the
 * background mixin ran, giving the custom border with the vanilla lines).
 *
 * This routes the NeoForge overload into the 6-argument method, where {@link FixToolTipMixin} does the work.
 */
@Mixin(GuiGraphicsExtractor.class)
public abstract class NeoForgeFixToolTipMixin {

    @Shadow
    public abstract void tooltip(Font font, List<ClientTooltipComponent> lines, int xo, int yo,
                                 ClientTooltipPositioner positioner, @Nullable Identifier style);

    @Unique
    private boolean amalgamation$routing;

    @Inject(
            method = "tooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/Identifier;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void amalgamation$routeToCustomTooltip(Font font, List<ClientTooltipComponent> lines, int xo, int yo,
                                                   ClientTooltipPositioner positioner, @Nullable Identifier style,
                                                   ItemStack tooltipStack, CallbackInfo ci) {
        // Re-entrancy guard: if the 6-arg method ever delegates back here, fall through to NeoForge's own drawing.
        if (this.amalgamation$routing) return;
        this.amalgamation$routing = true;
        try {
            this.tooltip(font, lines, xo, yo, positioner, style);
        } finally {
            this.amalgamation$routing = false;
        }
        ci.cancel();
    }
}
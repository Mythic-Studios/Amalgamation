package org.mythic_goose.amalgamation.mixin;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.FormattedCharSequence;
import org.mythic_goose.amalgamation.library.tooltip_v1.FormattedCharSequenceUtils;
import org.mythic_goose.amalgamation.library.tooltip_v1.RainbowTooltip;
import org.mythic_goose.amalgamation.library.tooltip_v1.RainbowTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTooltipComponent.class)
public interface ClientTooltipComponentMixin {
    @Inject(method = "create*", at = @At("HEAD"), cancellable = true)
    private static void rainbow$create(FormattedCharSequence seq, CallbackInfoReturnable<ClientTooltipComponent> cir) {
        String plain = FormattedCharSequenceUtils.toPlainText(seq);
        if (RainbowTooltip.hasMarker(plain)) {
            cir.setReturnValue(new RainbowTooltipComponent(RainbowTooltip.stripMarker(plain)));
        }
    }
}

package org.mythic_goose.amalgamation.mixin;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.FormattedCharSequence;
import org.mythic_goose.amalgamation.api.tooltip_v2.colors.FormattedCharSequenceUtils;
import org.mythic_goose.amalgamation.api.tooltip_v2.colors.RainbowTooltipComponent;
import org.mythic_goose.amalgamation.api.tooltip_v2.colors.TooltipParser;
import org.mythic_goose.amalgamation.api.tooltip_v2.colors.TooltipSegment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ClientTooltipComponent.class)
public interface ClientTooltipComponentMixin {
    @Inject(method = "create*", at = @At("HEAD"), cancellable = true)
    private static void colorText$create(FormattedCharSequence seq, CallbackInfoReturnable<ClientTooltipComponent> cir) {
        String plain = FormattedCharSequenceUtils.toPlainText(seq);
        List<TooltipSegment> segments = TooltipParser.parse(plain);
        if (segments != null) {
            cir.setReturnValue(new RainbowTooltipComponent(segments));
        }
    }
}

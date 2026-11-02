package org.mythic_goose.amalgamation.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import org.mythic_goose.amalgamation.api.tooltip_v2.panel.PanelContext;
import org.mythic_goose.amalgamation.api.tooltip_v2.panel.PanelDesign;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(TooltipRenderUtil.class)
public class TooltipBackgroundMixin {

    @Inject(method = "extractTooltipBackground", at = @At("HEAD"), cancellable = true)
    private static void amalgamation$customPanel(GuiGraphicsExtractor g, int x, int y, int w, int h,
                                                 net.minecraft.resources.Identifier style, CallbackInfo ci) {
        PanelDesign d = PanelContext.consumeDesign();
        if (d == null) return;

        long now = System.currentTimeMillis();
        int bgT = d.backgroundTop().argb(now);
        int bgB = d.backgroundBottom().argb(now);
        int brT = d.borderTop().argb(now);
        int brB = d.borderBottom().argb(now);

        // Background (same layout as the classic vanilla tooltip box)
        g.fill(x - 3, y - 4, x + w + 3, y - 3, bgT);
        g.fill(x - 3, y + h + 3, x + w + 3, y + h + 4, bgB);
        g.fillGradient(x - 3, y - 3, x + w + 3, y + h + 3, bgT, bgB);
        g.fillGradient(x - 4, y - 3, x - 3, y + h + 3, bgT, bgB);
        g.fillGradient(x + w + 3, y - 3, x + w + 4, y + h + 3, bgT, bgB);

        // Border
        g.fillGradient(x - 3, y - 2, x - 2, y + h + 2, brT, brB);
        g.fillGradient(x + w + 2, y - 2, x + w + 3, y + h + 2, brT, brB);
        g.fill(x - 3, y - 3, x + w + 3, y - 2, brT);
        g.fill(x - 3, y + h + 2, x + w + 3, y + h + 3, brB);

        ci.cancel();
    }
}
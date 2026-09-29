package org.mythic_goose.amalgamation.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2ic;
import org.jspecify.annotations.Nullable;
import org.mythic_goose.amalgamation.api.tooltip_v1.wrapping.TooltipHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public abstract class FixToolTipMixin {

    @Shadow
    public abstract int guiWidth();

    @Shadow
    public abstract int guiHeight();

    @Shadow
    @Final
    private Matrix3x2fStack pose;

    @ModifyVariable(
            method = "tooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/Identifier;)V",
            at = @At(value = "HEAD"),
            index = 2,
            argsOnly = true
    )
    public List<ClientTooltipComponent> makeListMutable(List<ClientTooltipComponent> value) {
        return new ArrayList<>(value);
    }

    @Inject(
            method = "tooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/Identifier;)V",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void fixAndReplace(Font font, List<ClientTooltipComponent> lines, int xo, int yo, ClientTooltipPositioner positioner, @Nullable Identifier style, CallbackInfo ci) {
        TooltipHelper.newFix(lines, font, xo, guiWidth());

        int textWidth = 0;
        int tempHeight = lines.size() == 1 ? -2 : 0;



        for (ClientTooltipComponent line : lines) {
            int lineWidth = line.getWidth(font);
            if (lineWidth > textWidth) {
                textWidth = lineWidth;
            }
            tempHeight += line.getHeight(font);
        }

        int w = textWidth;
        int h = tempHeight;
        Vector2ic positionedTooltip = positioner.positionTooltip(this.guiWidth(), this.guiHeight(), xo, yo, textWidth, tempHeight);
        int x = TooltipHelper.shouldFlip(lines, font, xo);
        int y = positionedTooltip.y();

        GuiGraphicsExtractor self = (GuiGraphicsExtractor) (Object) this;

        this.pose.pushMatrix();
        TooltipRenderUtil.extractTooltipBackground(self, x, y, textWidth, tempHeight, style);
        int localY = y;

        for (int i = 0; i < lines.size(); ++i) {
            ClientTooltipComponent line = lines.get(i);
            line.extractText(self, font, x, localY);
            localY += line.getHeight(font) + (i == 0 ? 2 : 0);
        }

        localY = y;
        for (int i = 0; i < lines.size(); ++i) {
            ClientTooltipComponent line = lines.get(i);
            line.extractImage(font, x, localY, w, h, self);
            localY += line.getHeight(font) + (i == 0 ? 2 : 0);
        }

        this.pose.popMatrix();

        ci.cancel();
    }
}
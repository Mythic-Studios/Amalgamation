package org.mythic_goose.amalgamation.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.mythic_goose.amalgamation.api.creative_tab_v1.BannerRenderer;
import org.mythic_goose.amalgamation.api.creative_tab_v1.TabLayout;
import org.mythic_goose.amalgamation.mixin.accessor.ItemPickerMenuAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {

    @Shadow
    private static CreativeModeTab selectedTab;

    // NOTE: verify this field name/type against your mappings - it's vanilla's
    // scroll-position field on CreativeModeInventoryScreen (float, 0.0-1.0).
    @Shadow
    private float scrollOffs;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void amalgamation$renderBanners(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TabLayout layout = TabLayout.forTab(selectedTab);
        if (layout == null) return;

        CreativeModeInventoryScreen self = (CreativeModeInventoryScreen)(Object) this;

        // Compute the row fresh, right now, from the same scroll state the vanilla
        // grid itself uses this frame - never a mirrored/stale copy.
        int currentRow = ((ItemPickerMenuAccessor) self.getMenu()).invokeGetRowIndexForScroll(scrollOffs);

        BannerRenderer.render(self, graphics, layout, currentRow);
    }
}
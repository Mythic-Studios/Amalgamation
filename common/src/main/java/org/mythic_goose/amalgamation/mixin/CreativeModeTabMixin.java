package org.mythic_goose.amalgamation.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.mythic_goose.amalgamation.api.creative_tab_v1.TabLayout;
import org.mythic_goose.amalgamation.mixin.accessor.CreativeModeTabAccessor;
import org.spongepowered.asm.mixin.Mixin;

import java.util.LinkedHashSet;
import java.util.List;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @WrapMethod(method = "buildContents")
    private void msgwoft$buildContents(CreativeModeTab.ItemDisplayParameters parameters, Operation<Void> original) {
        CreativeModeTab self = (CreativeModeTab)(Object) this;

        TabLayout layout = TabLayout.forTab(self);
        if (layout == null) {
            // Not a section-based tab (vanilla or another mod) - leave it untouched.
            original.call(parameters);
            return;
        }

        // Build ItemStacks HERE, lazily, when components are guaranteed bound
        List<ItemStack> display = layout.build();

        ((CreativeModeTabAccessor) self).setDisplayItems(display);
        ((CreativeModeTabAccessor) self).setDisplayItemsSearchTab(
                display.stream()
                        .filter(s -> !s.isEmpty())
                        .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new))
        );
    }
}
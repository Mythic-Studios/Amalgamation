package org.mythic_goose.amalgamation.api.creative_tab_v1;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

public interface CreativeTabRegistrar {
    Supplier<CreativeModeTab> register(String name, Supplier<CreativeModeTab> tabSupplier);
}
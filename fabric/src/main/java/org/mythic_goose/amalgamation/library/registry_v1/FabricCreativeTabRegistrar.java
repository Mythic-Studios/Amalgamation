package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import org.mythic_goose.amalgamation.library.creative_tab_v1.CreativeTabRegistrar;

import java.util.function.Supplier;

public class FabricCreativeTabRegistrar implements CreativeTabRegistrar {
    private final String modid;

    public FabricCreativeTabRegistrar(String modid) {
        this.modid = modid;
    }

    @Override
    public Supplier<CreativeModeTab> register(String name, Supplier<CreativeModeTab> tabSupplier) {
        Identifier id = Identifier.fromNamespaceAndPath(modid, name);
        CreativeModeTab registered = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, tabSupplier.get());
        return () -> registered;
    }
}
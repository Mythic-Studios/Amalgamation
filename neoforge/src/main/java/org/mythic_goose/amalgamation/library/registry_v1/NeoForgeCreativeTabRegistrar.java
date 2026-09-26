package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mythic_goose.amalgamation.library.creative_tab_v1.CreativeTabRegistrar;

import java.util.function.Supplier;

public class NeoForgeCreativeTabRegistrar implements CreativeTabRegistrar {
    private final DeferredRegister<CreativeModeTab> registry;

    public NeoForgeCreativeTabRegistrar(String modid) {
        this.registry = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modid);
    }

    @Override
    public Supplier<CreativeModeTab> register(String name, Supplier<CreativeModeTab> tabSupplier) {
        DeferredHolder<CreativeModeTab, CreativeModeTab> holder = registry.register(name, tabSupplier);
        return holder::get;
    }

    public void registerToBus(IEventBus bus) {
        registry.register(bus);
    }
}
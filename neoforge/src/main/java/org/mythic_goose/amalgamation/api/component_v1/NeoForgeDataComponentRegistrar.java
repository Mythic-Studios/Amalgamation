package org.mythic_goose.amalgamation.api.component_v1;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class NeoForgeDataComponentRegistrar implements DataComponentRegistrar {
    private final DeferredRegister<DataComponentType<?>> registry;

    public NeoForgeDataComponentRegistrar(String modid) {
        this.registry = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, modid);
    }

    @Override
    public <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> op) {
        DeferredHolder<DataComponentType<?>, DataComponentType<T>> holder =
                registry.register(name, () -> op.apply(DataComponentType.<T>builder()).build());
        return holder::get;
    }

    public void registerToBus(IEventBus bus) {
        registry.register(bus);
    }
}

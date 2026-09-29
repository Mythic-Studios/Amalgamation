package org.mythic_goose.amalgamation.api.component_v1;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class FabricDataComponentRegistrar implements DataComponentRegistrar {
    private final String modid;

    public FabricDataComponentRegistrar(String modid) {
        this.modid = modid;
    }

    @Override
    public <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> op) {
        DataComponentType<T> type = op.apply(DataComponentType.<T>builder()).build();
        DataComponentType<T> registered = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(modid, name),
                type);
        return () -> registered;
    }
}

package org.mythic_goose.amalgamation.api.component_v1;

import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface DataComponentRegistrar {
    <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> op);
}
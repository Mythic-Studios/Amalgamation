package org.mythic_goose.amalgamation.api.menu_v1;


import java.util.function.Supplier;

public interface MenuRegistrar {
    <T extends ExtendedMenuType<?, ?>> Supplier<T> register(String name, T type);
}
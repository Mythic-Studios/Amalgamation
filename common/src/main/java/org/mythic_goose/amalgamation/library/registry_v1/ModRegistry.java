package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.mythic_goose.amalgamation.Constants;
import org.mythic_goose.amalgamation.platform.Services;

import java.util.function.Supplier;

/**
 * The entry point every other {@code *Registry} helper routes registration through,
 * via {@link Services#PLATFORM}.
 */
public final class ModRegistry {
    public static String MOD_ID;

    private ModRegistry() {}

    static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    static <T> RegistryEntry<T> register(Registry<? super T> registry, String path, Supplier<T> factory) {
        return Services.PLATFORM.register(registry, id(path), factory);
    }
}
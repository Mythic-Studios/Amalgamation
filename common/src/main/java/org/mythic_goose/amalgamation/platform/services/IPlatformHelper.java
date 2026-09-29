package org.mythic_goose.amalgamation.platform.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.mythic_goose.amalgamation.api.registry_v1.RegistryEntry;

import java.util.function.Supplier;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /**
     * Registers {@code factory}'s product into {@code registry} under {@code id}, the way
     * this platform expects: immediately on Fabric, or deferred via {@code DeferredRegister}
     * on NeoForge (registries are frozen there outside a {@code RegisterEvent}).
     *
     * <p>{@code registry} accepts any supertype of {@code T} (e.g. {@code Registry<Item>}
     * when {@code T} is {@code BlockItem}), so the entry keeps its exact declared type.
     *
     * @param factory builds the entry - called immediately on Fabric; called once later
     *                (at actual registration time) on NeoForge. Don't reach into other
     *                not-yet-registered entries from outside this factory.
     */
    <T> RegistryEntry<T> register(Registry<? super T> registry, Identifier id, Supplier<T> factory);
}
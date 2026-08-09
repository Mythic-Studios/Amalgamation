package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

/**
 * The one loader-specific choke point every {@code *Registry} helper routes
 * registration through. Implement this once per loader:
 * <ul>
 *     <li>Fabric - register immediately, {@code Registry.register} is safe anytime.</li>
 *     <li>NeoForge - defer via {@code DeferredRegister}, since registries are frozen
 *         outside a {@code RegisterEvent}.</li>
 * </ul>
 * Publish your implementation via a
 * {@code META-INF/services/org.mythic_goose.terrific_creations.api.v1.registry.RegistrationBackend}
 * file in that loader's module, containing your implementation's fully-qualified name.
 */
public interface RegistrationBackend {

    /**
     * @param registry the vanilla registry to register into
     * @param id       the entry's id
     * @param factory  builds the entry - called immediately on Fabric; called once later
     *                 (at actual registration time) on NeoForge. Don't reach into other
     *                 not-yet-registered entries from outside this factory.
     */
    <T> RegistryEntry<T> register(Registry<T> registry, Identifier id, Supplier<T> factory);
}
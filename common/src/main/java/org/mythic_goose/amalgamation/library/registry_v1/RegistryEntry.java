package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

/**
 * A registered entry. Works identically whether it's backed by an already-resolved
 * value (Fabric) or a deferred one that only resolves once the registry event fires
 * (NeoForge) - just don't call {@link #get()} before your mod's registration phase has
 * actually run. Runtime code (item/block behavior, event handlers, etc.) is always safe;
 * another class's static initializer that might run first is not.
 */
public interface RegistryEntry<T> extends Supplier<T> {
    Identifier id();
}
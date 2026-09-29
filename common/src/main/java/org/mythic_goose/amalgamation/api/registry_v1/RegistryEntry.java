package org.mythic_goose.amalgamation.api.registry_v1;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public interface RegistryEntry<T> extends Supplier<T> {
    Identifier id();

    /**
     * Returns this entry as a {@link Holder}, for APIs that require one — e.g.
     * {@code new MobEffectInstance(SOME_EFFECT.asHolder(), ...)}. Like {@link #get()},
     * don't call this before your mod's registration phase has actually run.
     */
    Holder<T> asHolder();
}
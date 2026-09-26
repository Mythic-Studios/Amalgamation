package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public abstract class EffectRegistry {

    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT);

    /**
     * Registers a custom mob effect.
     *
     * @param path    effect name in snake_case
     * @param factory builds the effect
     * @return the registered effect entry
     */
    protected static RegistryEntry<MobEffect> registerMobEffect(String path, Supplier<MobEffect> factory) {
        return EFFECTS.register(path, factory);
    }
}
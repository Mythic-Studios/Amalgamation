package org.mythic_goose.amalgamation.api.registry_v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public abstract class EffectRegistry {

    protected static RegistryEntry<MobEffect> registerMobEffect(String path, Supplier<MobEffect> factory) {
        return DeferredRegister.<MobEffect>forCallingMod(BuiltInRegistries.MOB_EFFECT).register(path, factory);
    }
}
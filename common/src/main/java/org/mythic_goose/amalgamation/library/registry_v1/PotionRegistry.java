package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.alchemy.Potion;

/** Registry helper for potions. Works identically on Fabric and NeoForge. */
public abstract class PotionRegistry {

    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION);

    protected static RegistryEntry<Potion> registerPotion(String name, Potion potion) {
        return POTIONS.register(name, () -> potion);
    }
}
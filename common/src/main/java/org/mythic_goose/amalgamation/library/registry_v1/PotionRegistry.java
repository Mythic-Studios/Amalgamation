package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.alchemy.Potion;

/** Registry helper for potions. Works identically on Fabric and NeoForge. */
public abstract class PotionRegistry {

    protected static RegistryEntry<Potion> registerPotion(String name, Potion potion) {
        return ModRegistry.register(BuiltInRegistries.POTION, name, () -> potion);
    }
}
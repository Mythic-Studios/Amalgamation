package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

/** Registry helper for enchantments. Works identically on Fabric and NeoForge. */
public class EnchantmentRegistry {

    private static ResourceKey<Enchantment> registerKey(String id) {
        return ResourceKey.create(Registries.ENCHANTMENT, DeferredRegister.id(id));
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.identifier()));
    }
}
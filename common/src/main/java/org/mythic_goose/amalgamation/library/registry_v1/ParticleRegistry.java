package org.mythic_goose.amalgamation.library.registry_v1;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ParticleRegistry {

    private static SimpleParticleType registerParticle(String name, SimpleParticleType particleType) {
        Identifier id_path = ModRegistry.id(name);

        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, id_path, particleType);
    }
}

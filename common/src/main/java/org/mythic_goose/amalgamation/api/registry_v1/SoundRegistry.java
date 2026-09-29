package org.mythic_goose.amalgamation.api.registry_v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

/** Registry helper for sound events. Works identically on Fabric and NeoForge. */
public abstract class SoundRegistry {

    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT);

    /**
     * Registers the underlying {@link SoundEvent} for a jukebox song. Note this only
     * registers the sound - the jukebox song itself (length, output, comparator output)
     * is a separate data-driven entry in the jukebox_song registry that references this
     * sound's id, not something created in code.
     */
    protected static RegistryEntry<SoundEvent> registerJukeboxSong(String name) {
        return registerSoundEvent(name);
    }

    protected static RegistryEntry<SoundEvent> registerSoundEvent(String name) {
        Identifier id = DeferredRegister.id(name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
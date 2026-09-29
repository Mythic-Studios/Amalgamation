package org.mythic_goose.amalgamation.api.attachment_v1;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

/** Loader-agnostic description of an attachment — built once, handed to whichever platform impl is active. */
public final class AttachmentSpec<T> {
    public final Identifier id;
    public final Supplier<T> initialValue;
    public final Codec<T> persistentCodec;                                  // null = not saved
    public final StreamCodec<? super RegistryFriendlyByteBuf, T> syncCodec; // null = not synced
    public final boolean copyOnDeath;
    public final boolean syncToOwnerOnly;

    private AttachmentSpec(Builder<T> b) {
        this.id = b.id;
        this.initialValue = b.initialValue;
        this.persistentCodec = b.persistentCodec;
        this.syncCodec = b.syncCodec;
        this.copyOnDeath = b.copyOnDeath;
        this.syncToOwnerOnly = b.syncToOwnerOnly;
    }

    public static <T> Builder<T> builder(Identifier id, Supplier<T> initialValue) {
        return new Builder<>(id, initialValue);
    }

    public static final class Builder<T> {
        private final Identifier id;
        private final Supplier<T> initialValue;
        private Codec<T> persistentCodec;
        private StreamCodec<? super RegistryFriendlyByteBuf, T> syncCodec;
        private boolean copyOnDeath;
        private boolean syncToOwnerOnly = true;

        private Builder(Identifier id, Supplier<T> initialValue) {
            this.id = id;
            this.initialValue = initialValue;
        }

        public Builder<T> persistent(Codec<T> codec) { this.persistentCodec = codec; return this; }
        public Builder<T> syncWith(StreamCodec<? super RegistryFriendlyByteBuf, T> codec) { this.syncCodec = codec; return this; }
        public Builder<T> syncWith(StreamCodec<? super RegistryFriendlyByteBuf, T> codec, boolean ownerOnly) {
            this.syncCodec = codec; this.syncToOwnerOnly = ownerOnly; return this;
        }
        public Builder<T> copyOnDeath() { this.copyOnDeath = true; return this; }

        public AttachmentSpec<T> build() { return new AttachmentSpec<>(this); }
    }
}
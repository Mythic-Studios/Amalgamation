package org.mythic_goose.amalgamation.api.attachment_v1;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class NeoForgeAttachmentPlatform implements AttachmentPlatform {

    private static DeferredRegister<AttachmentType<?>> registry;

    public static void init(String modId, IEventBus modBus) {
        registry = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, modId);
        registry.register(modBus);
    }

    public NeoForgeAttachmentPlatform(String modId) {
        registry = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, modId);
    }

    @Override
    public <T> CommonAttachment<T> register(AttachmentSpec<T> spec) {
        Supplier<AttachmentType<T>> holder = registry.register(spec.id.getPath(), () -> {
            AttachmentType.Builder<T> builder = AttachmentType.builder(spec.initialValue);
            if (spec.persistentCodec != null) {
                // serialize() wants a MapCodec<T>, not a plain Codec<T> — wrap it in a field.
                builder.serialize(spec.persistentCodec.fieldOf("value"));
            }
            if (spec.copyOnDeath) builder.copyOnDeath();
            if (spec.syncCodec != null) builder.sync(spec.syncCodec);
            return builder.build();
        });

        return new CommonAttachment<T>() {
            @Override public T get(Entity entity) { return ((IAttachmentHolder) entity).getData(holder.get()); }
            @Override public void set(Entity entity, T value) { ((IAttachmentHolder) entity).setData(holder.get(), value); }
            @Override public boolean has(Entity entity) { return ((IAttachmentHolder) entity).hasData(holder.get()); }
            @Override public void remove(Entity entity) { ((IAttachmentHolder) entity).removeData(holder.get()); }
        };
    }
}
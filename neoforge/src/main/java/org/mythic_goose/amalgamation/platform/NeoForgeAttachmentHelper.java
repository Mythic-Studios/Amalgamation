package org.mythic_goose.amalgamation.platform;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.mythic_goose.amalgamation.api.attachment_v1.AttachmentSpec;
import org.mythic_goose.amalgamation.api.attachment_v1.CommonAttachment;
import org.mythic_goose.amalgamation.platform.services.IAttachmentHelper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgeAttachmentHelper implements IAttachmentHelper {

    private static final Map<String, DeferredRegister<AttachmentType<?>>> REGISTRIES = new ConcurrentHashMap<>();

    /** Call once from your mod's @Mod constructor, before registering any AttachmentSpec for that mod. */
    public static void init(String modId, IEventBus modBus) {
        DeferredRegister<AttachmentType<?>> registry = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, modId);
        registry.register(modBus);
        REGISTRIES.put(modId, registry);
    }

    @Override
    public <T> CommonAttachment<T> register(AttachmentSpec<T> spec) {
        String modId = spec.id.getNamespace();
        DeferredRegister<AttachmentType<?>> registry = REGISTRIES.get(modId);
        if (registry == null) {
            throw new IllegalStateException(
                    "NeoForgeAttachmentHelper.init(\"" + modId + "\", modBus) must be called before registering attachments for that mod");
        }

        Supplier<AttachmentType<T>> holder = registry.register(spec.id.getPath(), () -> {
            AttachmentType.Builder<T> builder = AttachmentType.builder(spec.initialValue);
            if (spec.persistentCodec != null) builder.serialize(spec.persistentCodec.fieldOf("value"));
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
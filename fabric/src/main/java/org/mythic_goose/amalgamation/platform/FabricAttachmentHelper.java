package org.mythic_goose.amalgamation.platform;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.Entity;
import org.mythic_goose.amalgamation.api.attachment_v1.AttachmentSpec;
import org.mythic_goose.amalgamation.api.attachment_v1.CommonAttachment;
import org.mythic_goose.amalgamation.platform.services.IAttachmentHelper;

public class FabricAttachmentHelper implements IAttachmentHelper {
    @Override
    public <T> CommonAttachment<T> register(AttachmentSpec<T> spec) {
        AttachmentType<T> type = AttachmentRegistry.create(spec.id, builder -> {
            builder.initializer(spec.initialValue::get);
            if (spec.persistentCodec != null) builder.persistent(spec.persistentCodec);
            if (spec.copyOnDeath) builder.copyOnDeath();
            if (spec.syncCodec != null) {
                builder.syncWith(spec.syncCodec,
                        spec.syncToOwnerOnly ? AttachmentSyncPredicate.targetOnly() : AttachmentSyncPredicate.all());
            }
        });

        return new CommonAttachment<T>() {
            @Override public T get(Entity entity) { return ((AttachmentTarget) entity).getAttachedOrCreate(type); }
            @Override public void set(Entity entity, T value) { ((AttachmentTarget) entity).setAttached(type, value); }
            @Override public boolean has(Entity entity) { return ((AttachmentTarget) entity).hasAttached(type); }
            @Override public void remove(Entity entity) { ((AttachmentTarget) entity).removeAttached(type); }
        };
    }
}
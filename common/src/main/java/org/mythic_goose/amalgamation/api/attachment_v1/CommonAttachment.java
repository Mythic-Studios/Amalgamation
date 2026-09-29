package org.mythic_goose.amalgamation.api.attachment_v1;

import net.minecraft.world.entity.Entity;

/** Loader-agnostic handle to a single attachment type. */
public interface CommonAttachment<T> {
    T get(Entity entity);
    void set(Entity entity, T value);
    boolean has(Entity entity);
    void remove(Entity entity);
}

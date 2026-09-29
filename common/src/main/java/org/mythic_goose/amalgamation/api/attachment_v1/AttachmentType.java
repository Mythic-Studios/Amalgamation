package org.mythic_goose.amalgamation.api.attachment_v1;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Identifies a type of data that can be attached to an {@link AttachmentHolder}.
 * <p>
 * Inspired by FabricMC's {@code AttachmentType} (Apache License 2.0), reworked here
 * as a plain, dependency-free class.
 *
 * @param <A> the type of the attached data
 */
public final class AttachmentType<A> {
    private final String id;
    private final Supplier<A> initializer;

    private AttachmentType(String id, Supplier<A> initializer) {
        this.id = id;
        this.initializer = initializer;
    }

    /**
     * Creates an attachment type with no default value. {@code getAttachedOrCreate(type)}
     * (single-arg) will throw for this type; use the two-arg overload instead.
     */
    public static <A> AttachmentType<A> create(String id) {
        return new AttachmentType<>(id, null);
    }

    /**
     * Creates an attachment type that lazily initializes to {@code initializer.get()}
     * the first time it's requested via {@code getAttachedOrCreate}.
     */
    public static <A> AttachmentType<A> createWithDefault(String id, Supplier<A> initializer) {
        Objects.requireNonNull(initializer, "initializer cannot be null");
        return new AttachmentType<>(id, initializer);
    }

    public Supplier<A> initializer() {
        return initializer;
    }

    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return "AttachmentType[" + id + "]";
    }
}


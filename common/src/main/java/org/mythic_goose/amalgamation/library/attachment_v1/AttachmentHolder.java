package org.mythic_goose.amalgamation.library.attachment_v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * A self-contained, mixin-free implementation of Fabric's {@code AttachmentTarget} pattern
 * (originally net.fabricmc.fabric.api.attachment.v1.AttachmentTarget, Apache License 2.0).
 * <p>
 * Unlike the Fabric API version, this class does NOT need to be injected into vanilla
 * classes via mixin — it holds its own backing map, so you can use it in any of these ways:
 * <ul>
 *   <li>Extend it directly: {@code class Thing extends AttachmentHolder { ... }}</li>
 *   <li>Compose it: give your class a {@code private final AttachmentHolder attachments = new AttachmentHolder();}
 *       field and delegate to it.</li>
 * </ul>
 * If you need to persist attachments (e.g. to NBT/CompoundTag) or mark an owning object dirty
 * when they change, override {@link #onChanged()}.
 */
public class AttachmentHolder {
    private final Map<AttachmentType<?>, Object> attachments = new HashMap<>();
    private final Map<AttachmentType<?>, List<OnAttachedSet<?>>> listeners = new HashMap<>();

    /** Gets the attached data, or {@code null} if none is present. */
    @SuppressWarnings("unchecked")
    public <A> A getAttached(AttachmentType<A> type) {
        return (A) attachments.get(type);
    }

    /** Gets the attached data, throwing if none is present. */
    public <A> A getAttachedOrThrow(AttachmentType<A> type) {
        return Objects.requireNonNull(getAttached(type), "No value was attached for " + type);
    }

    /** Gets the attached data, initializing (and storing) it with {@code defaultValue} if absent. */
    public <A> A getAttachedOrSet(AttachmentType<A> type, A defaultValue) {
        Objects.requireNonNull(defaultValue, "default value cannot be null");
        A attached = getAttached(type);

        if (attached != null) {
            return attached;
        }

        setAttached(type, defaultValue);
        return defaultValue;
    }

    /** Gets the attached data, initializing (and storing) it from {@code initializer} if absent. */
    public <A> A getAttachedOrCreate(AttachmentType<A> type, Supplier<A> initializer) {
        A attached = getAttached(type);

        if (attached != null) {
            return attached;
        }

        A initialized = Objects.requireNonNull(initializer.get(), "initializer result cannot be null");
        setAttached(type, initialized);
        return initialized;
    }

    /** Same as above, but uses the type's own default initializer. Throws if it doesn't have one. */
    public <A> A getAttachedOrCreate(AttachmentType<A> type) {
        Supplier<A> init = type.initializer();

        if (init == null) {
            throw new IllegalArgumentException(
                    "Single-argument getAttachedOrCreate is reserved for attachment types with default initializers");
        }

        return getAttachedOrCreate(type, init);
    }

    /** Gets the attached data, or {@code defaultValue} if absent. Does NOT store the default. */
    public <A> A getAttachedOrElse(AttachmentType<A> type, A defaultValue) {
        A attached = getAttached(type);
        return attached == null ? defaultValue : attached;
    }

    /** Gets the attached data, or lazily computes a fallback if absent. Does NOT store the result. */
    public <A> A getAttachedOrGet(AttachmentType<A> type, Supplier<A> defaultValue) {
        Objects.requireNonNull(defaultValue, "default value supplier cannot be null");
        A attached = getAttached(type);
        return attached == null ? defaultValue.get() : attached;
    }

    /** Sets the attached data. Passing {@code null} removes it. Returns the previous value. */
    @SuppressWarnings("unchecked")
    public <A> A setAttached(AttachmentType<A> type, A value) {
        A oldValue;

        if (value == null) {
            oldValue = (A) attachments.remove(type);
        } else {
            oldValue = (A) attachments.put(type, value);
        }

        onChanged();
        fireListeners(type, oldValue, value);
        return oldValue;
    }

    /** Whether this holder currently has data for the given type. */
    public boolean hasAttached(AttachmentType<?> type) {
        return attachments.containsKey(type);
    }

    /** Removes and returns any data associated with the given type. */
    public <A> A removeAttached(AttachmentType<A> type) {
        return setAttached(type, null);
    }

    /** Applies {@code modifier} to the current value (which may be {@code null}) and stores the result. */
    public <A> A modifyAttached(AttachmentType<A> type, UnaryOperator<A> modifier) {
        return setAttached(type, modifier.apply(getAttached(type)));
    }

    /** Registers a listener that fires after {@code type}'s value changes on this holder. */
    public <A> void addAttachedSetListener(AttachmentType<A> type, OnAttachedSet<A> listener) {
        listeners.computeIfAbsent(type, t -> new ArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    private <A> void fireListeners(AttachmentType<A> type, A oldValue, A newValue) {
        List<OnAttachedSet<?>> list = listeners.get(type);

        if (list == null) {
            return;
        }

        // copy to avoid ConcurrentModificationException if a listener adds/removes listeners
        for (OnAttachedSet<?> listener : new ArrayList<>(list)) {
            ((OnAttachedSet<A>) listener).onAttachedSet(oldValue, newValue);
        }
    }

    /**
     * Called after any attachment on this holder changes. Override this to hook into your own
     * "mark dirty" / save logic (e.g. call {@code setChanged()} on a block entity, sync to
     * clients, write to NBT, etc). No-op by default.
     */
    protected void onChanged() {
    }

    @FunctionalInterface
    public interface OnAttachedSet<A> {
        /**
         * @param oldValue the value before the change (may be {@code null})
         * @param newValue the value after the change (may be {@code null} if removed)
         */
        void onAttachedSet(A oldValue, A newValue);
    }
}

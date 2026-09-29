package org.mythic_goose.amalgamation.api.attachment_v1;

/** One implementation per loader — wired up via your existing expect/actual or ServiceLoader mechanism. */
public interface AttachmentPlatform {
    <T> CommonAttachment<T> register(AttachmentSpec<T> spec);
}
package org.mythic_goose.amalgamation.platform.services;


import org.mythic_goose.amalgamation.library.attachment_v1.AttachmentSpec;
import org.mythic_goose.amalgamation.library.attachment_v1.CommonAttachment;

public interface IAttachmentHelper {
    <T> CommonAttachment<T> register(AttachmentSpec<T> spec);
}
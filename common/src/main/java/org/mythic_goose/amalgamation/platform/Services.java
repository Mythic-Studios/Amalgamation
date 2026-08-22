package org.mythic_goose.amalgamation.platform;

import org.mythic_goose.amalgamation.AmalgamationConstants;
import org.mythic_goose.amalgamation.platform.services.IAttachmentHelper;
import org.mythic_goose.amalgamation.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IAttachmentHelper ATTACHMENTS = load(IAttachmentHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        AmalgamationConstants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
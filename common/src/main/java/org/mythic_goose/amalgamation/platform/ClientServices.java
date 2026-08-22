package org.mythic_goose.amalgamation.platform;

import org.mythic_goose.amalgamation.AmalgamationConstants;
import org.mythic_goose.amalgamation.platform.services.IItemTooltipHelper;

import java.util.ServiceLoader;

public class ClientServices {

    public static final IItemTooltipHelper ITEM_TOOLTIPS = load(IItemTooltipHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz, ClientServices.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        AmalgamationConstants.LOG.debug("Loaded {} for client service {}", loadedService, clazz);
        return loadedService;
    }
}
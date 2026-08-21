package org.mythic_goose.amalgamation;

import org.mythic_goose.amalgamation.platform.Services;


public class AmalgamationCore {
    public static void init() {

        AmalgamationConstants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());


        if (Services.PLATFORM.isModLoaded("amalgamation")) {

            AmalgamationConstants.LOG.info("Hello to amalgamation");
        }
    }
}
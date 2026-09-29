package org.mythic_goose.amalgamation;

import org.mythic_goose.amalgamation.api.menu_v1.ExtendedMenuNetworking;
import org.mythic_goose.amalgamation.platform.Services;


public class AmalgamationCore {
    public static void init() {
        ExtendedMenuNetworking.register();

        if (Services.PLATFORM.isModLoaded("amalgamation")) {
            // TODO - Improve this section a bit
        }
    }
}
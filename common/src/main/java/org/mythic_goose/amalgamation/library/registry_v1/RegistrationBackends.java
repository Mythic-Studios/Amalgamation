package org.mythic_goose.amalgamation.library.registry_v1;

import java.util.ServiceLoader;

/** Loads whichever {@link RegistrationBackend} the current loader's module provides. */
public final class RegistrationBackends {

    private static volatile RegistrationBackend instance;

    private RegistrationBackends() {}

    public static RegistrationBackend get() {
        RegistrationBackend result = instance;
        if (result == null) {
            synchronized (RegistrationBackends.class) {
                result = instance;
                if (result == null) {
                    result = ServiceLoader.load(RegistrationBackend.class)
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException(
                                    "No RegistrationBackend found on the classpath. Add a "
                                            + "META-INF/services/" + RegistrationBackend.class.getName()
                                            + " file in your Fabric or NeoForge module naming your backend implementation."));
                    instance = result;
                }
            }
        }
        return result;
    }
}
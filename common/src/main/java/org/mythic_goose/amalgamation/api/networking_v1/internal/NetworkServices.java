package org.mythic_goose.amalgamation.api.networking_v1.internal;

import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

@ApiStatus.Internal
public final class NetworkServices {
    public static final NetworkPlatform PLATFORM = ServiceLoader
            .load(NetworkPlatform.class, NetworkServices.class.getClassLoader())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No NetworkPlatform implementation found"));

    private NetworkServices() {}
}
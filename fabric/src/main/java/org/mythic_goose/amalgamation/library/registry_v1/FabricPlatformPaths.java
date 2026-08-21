package org.mythic_goose.amalgamation.library.registry_v1;

import net.fabricmc.loader.api.FabricLoader;
import org.mythic_goose.amalgamation.library.registry_v1.interfaces.PlatformPaths;

import java.nio.file.Path;

public class FabricPlatformPaths implements PlatformPaths {
    public Path getGameConfigDir() { return FabricLoader.getInstance().getConfigDir(); }
}

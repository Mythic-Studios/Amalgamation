package org.mythic_goose.amalgamation.platform;

import net.neoforged.fml.loading.FMLPaths;
import org.mythic_goose.amalgamation.api.registry_v1.interfaces.PlatformPaths;

import java.nio.file.Path;

public class ForgePlatformPaths implements PlatformPaths {
    public Path getGameConfigDir() { return FMLPaths.CONFIGDIR.get(); }
}
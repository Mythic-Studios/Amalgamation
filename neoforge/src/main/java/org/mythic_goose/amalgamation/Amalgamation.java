package org.mythic_goose.amalgamation;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.mythic_goose.amalgamation.platform.NeoForgePlatformHelper;

@Mod(Constants.MOD_ID)
public class Amalgamation {

    public Amalgamation(IEventBus eventBus) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.
        NeoForgePlatformHelper.init(eventBus);

        // Use NeoForge to bootstrap the Common mod.
        Constants.LOG.info("Hello NeoForge world!");
        AmalgamationCore.init();

    }
}
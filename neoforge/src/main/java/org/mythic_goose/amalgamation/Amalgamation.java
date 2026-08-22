package org.mythic_goose.amalgamation;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.mythic_goose.amalgamation.library.attachment_v1.NeoForgeAttachmentPlatform;
import org.mythic_goose.amalgamation.platform.NeoForgeAttachmentHelper;
import org.mythic_goose.amalgamation.platform.NeoForgePlatformHelper;

@Mod(AmalgamationConstants.MOD_ID)
public class Amalgamation {

    public Amalgamation(IEventBus eventBus) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.
        NeoForgePlatformHelper.init(eventBus);
        NeoForgeAttachmentPlatform.init(eventBus);
        NeoForgeAttachmentHelper.init(AmalgamationConstants.MOD_ID, eventBus);

        // Use NeoForge to bootstrap the Common mod.
        AmalgamationConstants.LOG.info("Hello NeoForge world!");
        AmalgamationCore.init();

    }
}
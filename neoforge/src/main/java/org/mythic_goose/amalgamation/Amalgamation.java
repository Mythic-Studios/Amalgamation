package org.mythic_goose.amalgamation;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.mythic_goose.amalgamation.api.attachment_v1.NeoForgeAttachmentPlatform;
import org.mythic_goose.amalgamation.api.menu_v1.ExtendedMenuOpenPayload;
import org.mythic_goose.amalgamation.api.networking_v1.NeoForgeNetworkPlatform;
import org.mythic_goose.amalgamation.platform.NeoForgeAttachmentHelper;
import org.mythic_goose.amalgamation.platform.NeoForgePlatformHelper;

@Mod(AmalgamationConstants.MOD_ID)
public class Amalgamation {

    public Amalgamation(IEventBus eventBus) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.
        NeoForgePlatformHelper.init(eventBus);
        NeoForgeAttachmentPlatform.init(AmalgamationConstants.MOD_ID,eventBus);
        NeoForgeAttachmentHelper.init(AmalgamationConstants.MOD_ID, eventBus);
        eventBus.addListener(NeoForgeNetworkPlatform::onRegisterPayloads);

        // Use NeoForge to bootstrap the Common mod.
        AmalgamationConstants.LOG.info("Hello NeoForge world!");
        AmalgamationCore.init();

    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                ExtendedMenuOpenPayload.TYPE,
                ExtendedMenuOpenPayload.CODEC,
                (payload, ctx) -> { /* client handler: open the screen */ }
        );
    }
}
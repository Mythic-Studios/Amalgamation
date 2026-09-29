package org.mythic_goose.amalgamation.api.networking_v1;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor; // verify import in your IDE

final class NeoForgeClientHooks {
    private NeoForgeClientHooks() {}

    static void send(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }
}
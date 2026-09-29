package org.mythic_goose.amalgamation.api.networking_v1;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.mythic_goose.amalgamation.api.networking_v1.internal.NetworkDispatch;

// Isolated so client-only classes never load on a dedicated server.
final class FabricClientHooks {
    private FabricClientHooks() {}

    static <T extends CustomPacketPayload> void registerReceiver(CustomPacketPayload.Type<T> type) {
        ClientPlayNetworking.registerGlobalReceiver(type,
                (payload, ctx) -> NetworkDispatch.toClient(payload, ctx.player(), ctx.client()));
    }

    static void send(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
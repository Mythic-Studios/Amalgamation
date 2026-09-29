package org.mythic_goose.amalgamation.api.networking_v1;

import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.mythic_goose.amalgamation.api.networking_v1.internal.NetworkDispatch;
import org.mythic_goose.amalgamation.api.networking_v1.internal.NetworkServices;

public final class ServerPlayNetworking {
    private ServerPlayNetworking() {}

    public static <T extends CustomPacketPayload> void registerGlobalReceiver(
            CustomPacketPayload.Type<T> type, PlayPayloadHandler<T> handler) {
        NetworkDispatch.addServer(type, handler);
    }

    public static void send(ServerPlayer player, CustomPacketPayload payload) {
        NetworkServices.PLATFORM.sendToPlayer(player, payload);
    }

    public static void sendToAll(MinecraftServer server, CustomPacketPayload payload) {
        server.getPlayerList().broadcastAll(new ClientboundCustomPayloadPacket(payload));
    }

    public static void sendToTracking(Entity entity, CustomPacketPayload payload) {
        if (entity.level().getChunkSource() instanceof ServerChunkCache cache) {
            cache.sendToTrackingPlayers(entity, new ClientboundCustomPayloadPacket(payload));
        }
    }

    @FunctionalInterface
    public interface PlayPayloadHandler<T extends CustomPacketPayload> {
        void receive(T payload, Context context);
    }

    public record Context(ServerPlayer player) {
        /** Runs the task on the server main thread. */
        public void execute(Runnable task) {
            player.level().getServer().execute(task);
        }
    }
}
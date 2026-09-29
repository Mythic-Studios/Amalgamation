package org.mythic_goose.amalgamation.api.networking_v1;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.mythic_goose.amalgamation.api.networking_v1.internal.NetworkDispatch;
import org.mythic_goose.amalgamation.api.networking_v1.internal.NetworkServices;

import java.util.concurrent.Executor;

public final class ClientPlayNetworking {
    private ClientPlayNetworking() {}

    public static <T extends CustomPacketPayload> void registerGlobalReceiver(
            CustomPacketPayload.Type<T> type, PlayPayloadHandler<T> handler) {
        NetworkDispatch.addClient(type, handler);
    }

    public static void send(CustomPacketPayload payload) {
        NetworkServices.PLATFORM.sendToServer(payload);
    }

    @FunctionalInterface
    public interface PlayPayloadHandler<T extends CustomPacketPayload> {
        void receive(T payload, Context context);
    }

    public record Context(Player player, Executor executor) {
        /** Runs the task on the client main thread. */
        public void execute(Runnable task) {
            executor.execute(task);
        }
    }
}
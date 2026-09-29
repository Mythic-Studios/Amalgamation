package org.mythic_goose.amalgamation.api.networking_v1.internal;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.mythic_goose.amalgamation.api.networking_v1.ClientPlayNetworking;
import org.mythic_goose.amalgamation.api.networking_v1.ServerPlayNetworking;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@ApiStatus.Internal
public final class NetworkDispatch {
    private static final Map<CustomPacketPayload.Type<?>, ServerPlayNetworking.PlayPayloadHandler<?>> SERVER = new ConcurrentHashMap<>();
    private static final Map<CustomPacketPayload.Type<?>, ClientPlayNetworking.PlayPayloadHandler<?>> CLIENT = new ConcurrentHashMap<>();

    private NetworkDispatch() {}

    public static void addServer(CustomPacketPayload.Type<?> type, ServerPlayNetworking.PlayPayloadHandler<?> handler) {
        if (SERVER.putIfAbsent(type, handler) != null) {
            throw new IllegalStateException("Server receiver already registered for " + type.id());
        }
    }

    public static void addClient(CustomPacketPayload.Type<?> type, ClientPlayNetworking.PlayPayloadHandler<?> handler) {
        if (CLIENT.putIfAbsent(type, handler) != null) {
            throw new IllegalStateException("Client receiver already registered for " + type.id());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomPacketPayload> void toServer(T payload, ServerPlayer player) {
        var handler = (ServerPlayNetworking.PlayPayloadHandler<T>) SERVER.get(payload.type());
        if (handler != null) {
            handler.receive(payload, new ServerPlayNetworking.Context(player));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomPacketPayload> void toClient(T payload, Player player, Executor mainThread) {
        var handler = (ClientPlayNetworking.PlayPayloadHandler<T>) CLIENT.get(payload.type());
        if (handler != null) {
            handler.receive(payload, new ClientPlayNetworking.Context(player, mainThread));
        }
    }
}
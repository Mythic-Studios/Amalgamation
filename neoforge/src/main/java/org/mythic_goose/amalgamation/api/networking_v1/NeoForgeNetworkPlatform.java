package org.mythic_goose.amalgamation.api.networking_v1;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.mythic_goose.amalgamation.api.networking_v1.internal.NetworkDispatch;
import org.mythic_goose.amalgamation.api.networking_v1.internal.NetworkPlatform;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeNetworkPlatform implements NetworkPlatform {
    private static final List<Entry<?>> PENDING = new ArrayList<>();
    private static volatile boolean frozen = false;

    @Override
    public <T extends CustomPacketPayload> void registerPayload(
            PacketFlow flow, CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        if (frozen) {
            throw new IllegalStateException("Payload " + type.id() + " was registered too late. "
                    + "Register networking payloads in your mod constructor.");
        }
        synchronized (PENDING) {
            PENDING.add(new Entry<>(flow, type, codec));
        }
    }

    /** Listener for the MOD event bus. */
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        synchronized (PENDING) {
            PENDING.forEach(e -> e.apply(registrar));
            PENDING.clear();
            frozen = true;
        }
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        NeoForgeClientHooks.send(payload);
    }

    private record Entry<T extends CustomPacketPayload>(
            PacketFlow flow, CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {

        void apply(PayloadRegistrar registrar) {
            if (flow == PacketFlow.SERVERBOUND) {
                registrar.playToServer(type, codec, (payload, ctx) ->
                        NetworkDispatch.toServer(payload, (ServerPlayer) ctx.player()));
            } else {
                registrar.playToClient(type, codec, (payload, ctx) ->
                        NetworkDispatch.toClient(payload, ctx.player(), ctx::enqueueWork));
            }
        }
    }
}
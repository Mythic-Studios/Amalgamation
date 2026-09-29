package org.mythic_goose.amalgamation.api.networking_v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.mythic_goose.amalgamation.api.networking_v1.internal.NetworkDispatch;
import org.mythic_goose.amalgamation.api.networking_v1.internal.NetworkPlatform;

public class FabricNetworkPlatform implements NetworkPlatform {

    @Override
    public <T extends CustomPacketPayload> void registerPayload(
            PacketFlow flow, CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        if (flow == PacketFlow.SERVERBOUND) {
            PayloadTypeRegistry.serverboundPlay().register(type, codec);
            ServerPlayNetworking.registerGlobalReceiver(type,
                    (payload, ctx) -> NetworkDispatch.toServer(payload, ctx.player()));
        } else {
            PayloadTypeRegistry.clientboundPlay().register(type, codec);
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                FabricClientHooks.registerReceiver(type);
            }
        }
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        FabricClientHooks.send(payload);
    }
}
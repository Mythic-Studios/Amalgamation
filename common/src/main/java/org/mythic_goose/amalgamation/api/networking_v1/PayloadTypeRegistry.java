package org.mythic_goose.amalgamation.api.networking_v1;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.mythic_goose.amalgamation.api.networking_v1.internal.NetworkServices;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class PayloadTypeRegistry {
    private static final PayloadTypeRegistry SERVERBOUND = new PayloadTypeRegistry(PacketFlow.SERVERBOUND);
    private static final PayloadTypeRegistry CLIENTBOUND = new PayloadTypeRegistry(PacketFlow.CLIENTBOUND);

    private final PacketFlow flow;
    private final Set<CustomPacketPayload.Type<?>> registered = ConcurrentHashMap.newKeySet();

    private PayloadTypeRegistry(PacketFlow flow) {
        this.flow = flow;
    }

    public static PayloadTypeRegistry serverboundPlay() {
        return SERVERBOUND;
    }

    public static PayloadTypeRegistry clientboundPlay() {
        return CLIENTBOUND;
    }

    /**
     * Registers a payload type.
     * <p>
     * <b>Must be called from your mod constructor</b> (or code it calls). On NeoForge,
     * registrations after the payload registration event are rejected.
     */
    public <T extends CustomPacketPayload> void register(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        if (!registered.add(type)) {
            throw new IllegalArgumentException("Payload already registered: " + type.id());
        }
        NetworkServices.PLATFORM.registerPayload(flow, type, codec);
    }
}
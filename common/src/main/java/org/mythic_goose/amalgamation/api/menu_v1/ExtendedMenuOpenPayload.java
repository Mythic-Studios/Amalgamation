package org.mythic_goose.amalgamation.api.menu_v1;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.mythic_goose.amalgamation.AmalgamationConstants;

public record ExtendedMenuOpenPayload(int syncId, Identifier menuTypeId, Component title, byte[] data)
        implements CustomPacketPayload {

    public static final Type<ExtendedMenuOpenPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(AmalgamationConstants.MOD_ID, "extended_menu_open"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExtendedMenuOpenPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ExtendedMenuOpenPayload::syncId,
                    Identifier.STREAM_CODEC, ExtendedMenuOpenPayload::menuTypeId,
                    ComponentSerialization.STREAM_CODEC, ExtendedMenuOpenPayload::title,
                    ByteBufCodecs.BYTE_ARRAY, ExtendedMenuOpenPayload::data,
                    ExtendedMenuOpenPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
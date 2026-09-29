package org.mythic_goose.amalgamation.api.menu_v1;

import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.mythic_goose.amalgamation.api.networking_v1.ClientPlayNetworking;
import org.mythic_goose.amalgamation.api.networking_v1.PayloadTypeRegistry;
import org.mythic_goose.amalgamation.api.networking_v1.ServerPlayNetworking;

public final class ExtendedMenuNetworking {
    private ExtendedMenuNetworking() {}

    /** Called once by Amalgamation's own init, from the mod constructor. */
    @ApiStatus.Internal
    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(ExtendedMenuOpenPayload.TYPE, ExtendedMenuOpenPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ExtendedMenuOpenPayload.TYPE, (payload, ctx) ->
                ctx.execute(() -> ExtendedMenuClient.handle(payload, ctx.player())));
    }

    static <D> void open(ServerPlayer player, int syncId, ExtendedMenuType<?, D> type,
                         Component title, D data) {
        Identifier id = BuiltInRegistries.MENU.getKey(type);
        if (id == null) {
            throw new IllegalStateException("ExtendedMenuType is not registered: " + type);
        }

        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.level().registryAccess());
        type.getStreamCodec().encode(buf, data);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.release();

        ServerPlayNetworking.send(player, new ExtendedMenuOpenPayload(syncId, id, title, bytes));
    }
}
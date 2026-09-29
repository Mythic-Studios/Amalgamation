package org.mythic_goose.amalgamation.api.menu_v1;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.Optional;

final class ExtendedMenuClient {
    private ExtendedMenuClient() {}

    static void handle(ExtendedMenuOpenPayload payload, Player player) {
        Optional<Holder.Reference<MenuType<?>>> holder = BuiltInRegistries.MENU.get(payload.menuTypeId());
        if (holder.isEmpty() || !(holder.get().value() instanceof ExtendedMenuType<?, ?> extendedType)) {
            return;
        }

        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                Unpooled.wrappedBuffer(payload.data()), player.level().registryAccess());
        Object data = extendedType.getStreamCodec().decode(buf);

        openUnchecked(extendedType, payload.syncId(), player, payload.title(), data);
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractContainerMenu, D> void openUnchecked(
            ExtendedMenuType<T, D> type, int syncId, Player player, Component title, Object data) {
        Inventory inventory = player.getInventory();
        T menu = type.create(syncId, inventory, (D) data);
        Screen screen = type.createScreen(menu, inventory, title);
        Minecraft.getInstance().gui.setScreen(screen);
        if (player instanceof LocalPlayer local) {
            local.containerMenu = menu;
        }
    }
}
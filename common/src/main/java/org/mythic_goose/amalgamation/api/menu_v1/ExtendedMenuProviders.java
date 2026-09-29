package org.mythic_goose.amalgamation.api.menu_v1;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public final class ExtendedMenuProviders {
    private ExtendedMenuProviders() {}

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private static int nextSyncId() {
        return COUNTER.updateAndGet(i -> i % 100 + 1);
    }

    private static final Method INIT_MENU;
    static {
        try {
            INIT_MENU = ServerPlayer.class.getDeclaredMethod("initMenu", AbstractContainerMenu.class);
            INIT_MENU.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static <T extends AbstractContainerMenu, D> void open(
            ServerPlayer player, ExtendedMenuType<T, D> type, ExtendedMenuProvider<D> provider) {

        if (player.containerMenu != player.inventoryMenu) {
            player.closeContainer();
        }

        int syncId = nextSyncId();
        D data = provider.getScreenOpeningData(player);
        T menu = type.create(syncId, player.getInventory(), data);
        if (menu == null) return;

        // Send our open payload first. Deliberately delay initMenu()/assigning
        // containerMenu by one server tick — without this, vanilla's own data-sync
        // packets (sent the moment broadcastChanges starts running for this menu) can
        // race ahead of the client finishing our custom open payload. The client
        // silently drops any data-sync packet whose containerId doesn't match its
        // currently-known menu, so a value that only ever changes once (like max page)
        // would be lost permanently if that race is lost.
        ExtendedMenuNetworking.open(player, syncId, type, provider.getDisplayName(), data);

        player.server.execute(() -> {
            try {
                INIT_MENU.invoke(player, menu);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to init extended menu " + type, e);
            }
            player.containerMenu = menu;
        });
    }
}
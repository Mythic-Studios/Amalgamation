/*
 * Adapted from FabricMC's fabric-api (net.fabricmc.fabric.api.menu.v1.ExtendedMenuType).
 * Original copyright (c) 2016, 2017, 2018, 2019 FabricMC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This version has been stripped of any Fabric-specific package coupling so it
 * can be dropped into a common / multi-loader module and reused on Fabric,
 * Forge, or NeoForge alike. It depends only on vanilla Minecraft classes.
 */

package org.mythic_goose.amalgamation.api.menu_v1;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.Objects;

/**
 * A {@link MenuType} for extended menus that synchronize additional data to the
 * client when opened. Loader-agnostic: only vanilla Minecraft types are used, so
 * this class can be shared across Fabric, Forge, and NeoForge common code.
 *
 * <p>How you actually get the {@code data} object to sync will depend on the
 * platform you're on (Fabric has {@code ExtendedScreenHandlerFactory}-style
 * hooks; Forge/NeoForge use {@code IContainerFactory} + their own networking).
 * This class only handles the vanilla-side {@link MenuType} plumbing, i.e.
 * constructing the menu on both sides from a decoded {@code D} value.
 *
 * <h2>Example</h2>
 * <pre>
 * {@code
 * // Data class
 * public record OvenData(String label) {
 *     public static final StreamCodec<RegistryFriendlyByteBuf, OvenData> STREAM_CODEC = StreamCodec.composite(
 *         ByteBufCodecs.STRING_UTF8,
 *         OvenData::label,
 *         OvenData::new
 *     );
 * }
 *
 * // Creating and registering the type
 * public static final ExtendedMenuType<OvenMenu, OvenData> OVEN =
 *     new ExtendedMenuType<>((containerId, inventory, data) -> new OvenMenu(containerId, inventory, data), OvenData.STREAM_CODEC);
 * // Register OVEN with whatever registry mechanism your loader uses.
 *
 * // Menu class
 * public class OvenMenu extends AbstractContainerMenu {
 *     public OvenMenu(int containerId, Inventory inventory, OvenData data) {
 *         super(MyMenus.OVEN, containerId);
 *         // use data.label() etc.
 *     }
 * }
 * }
 * </pre>
 *
 * @param <T> the type of menu created by this type
 * @param <D> the type of the synced opening data
 */
public class ExtendedMenuType<T extends AbstractContainerMenu, D> extends MenuType<T> {
    private final ExtendedFactory<T, D> factory;
    private final StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec;
    private ScreenConstructor<T, ?> screenConstructor;

    /**
     * Constructs an extended menu type.
     *
     * @param factory     the menu factory used for {@link #create(int, Inventory, Object)}
     * @param streamCodec the codec used to encode/decode the opening data {@code D}
     */
    public ExtendedMenuType(ExtendedFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec) {
        super(null, FeatureFlags.VANILLA_SET);
        this.factory = Objects.requireNonNull(factory, "menu factory cannot be null");
        this.streamCodec = Objects.requireNonNull(streamCodec, "stream codec cannot be null");
    }

    /**
     * @throws UnsupportedOperationException always; use {@link #create(int, Inventory, Object)}
     * @deprecated Use {@link #create(int, Inventory, Object)} instead.
     */
    @Deprecated
    @Override
    public final T create(int containerId, Inventory inventory) {
        throw new UnsupportedOperationException("Use ExtendedMenuType.create(int, Inventory, Object) with the decoded data instead!");
    }

    /**
     * Creates a new menu using the extra opening data.
     *
     * @param containerId the container ID
     * @param inventory   the player inventory
     * @param data        the synced opening data
     * @return the created menu
     */
    public T create(int containerId, Inventory inventory, D data) {
        return factory.create(containerId, inventory, data);
    }

    /**
     * @return the stream codec used to serialize/deserialize the opening data of this menu
     */
    public StreamCodec<? super RegistryFriendlyByteBuf, D> getStreamCodec() {
        return streamCodec;
    }

    /**
     * Registers the client-side screen for this menu type. Call this from your
     * CLIENT-only init, mirroring vanilla's MenuScreens.register(...) but stored
     * here instead, since this type never goes through MenuScreens' own lookup.
     */
    public <U extends Screen & MenuAccess<T>> void registerScreen(ScreenConstructor<T, U> constructor) {
        this.screenConstructor = constructor;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Screen createScreen(T menu, Inventory inventory, Component title) {
        if (screenConstructor == null) {
            throw new IllegalStateException("No screen registered for " + this);
        }
        return ((ScreenConstructor) screenConstructor).create(menu, inventory, title);
    }

    @FunctionalInterface
    public interface ScreenConstructor<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
        U create(T menu, Inventory inventory, Component title);
    }


    /**
     * A factory for creating menu instances from additional opening data.
     * Called on the client after decoding the synced data, but nothing stops
     * you from calling it on the server too.
     *
     * @param <T> the type of menu created
     * @param <D> the type of the data
     * @see #create(int, Inventory, Object)
     */
    @FunctionalInterface
    public interface ExtendedFactory<T extends AbstractContainerMenu, D> {
        /**
         * Creates a new menu with additional screen-opening data.
         *
         * @param containerId the container ID
         * @param inventory   the player inventory
         * @param data        the synced data
         * @return the created menu
         */
        T create(int containerId, Inventory inventory, D data);
    }
}
/*
 * Adapted from FabricMC's fabric-api (net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider).
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


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

/**
 * An extension of {@link MenuProvider} that can supply additional data to be sent
 * to the client alongside the screen-opening packet. Used together with
 * {@linkplain ExtendedMenuType extended menu types}.
 *
 * <p>Loader-agnostic: only vanilla Minecraft types are used here. How the returned
 * data is actually serialized and sent depends on your platform's networking layer
 * (Fabric, Forge, and NeoForge each have their own way of triggering a menu-open
 * packet with extra data) — this interface just standardizes where that data comes
 * from on the server side.
 *
 * @param <D> the type of the opening data supplied to the client
 * @see ExtendedMenuType usage examples
 */
public interface ExtendedMenuProvider<D> extends MenuProvider {
    /**
     * Supplies the server -&gt; client screen opening data for the given player.
     * Called when the menu is about to be opened, so the returned value should
     * reflect this provider's state at that moment.
     *
     * @param player the player that is opening the screen
     * @return the screen opening data, to be encoded with the matching
     *         {@link ExtendedMenuType}'s stream codec
     */
    D getScreenOpeningData(ServerPlayer player);
}


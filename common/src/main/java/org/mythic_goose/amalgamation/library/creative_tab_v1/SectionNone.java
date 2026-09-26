package org.mythic_goose.amalgamation.library.creative_tab_v1;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;

/**
 * A header-less section: no banner, no reserved row, items sit flush at the top of the tab
 * exactly like vanilla's creative inventory. See {@link SectionStyle#none()}.
 */
public record SectionNone(String id, List<Item> items) implements Section {

    @Override
    public Component title() {
        return Component.empty();
    }

    @Override
    public int textColor() {
        return 0xFFFFFFFF;
    }

    @Override
    public boolean hasHeader() {
        return false;
    }
}
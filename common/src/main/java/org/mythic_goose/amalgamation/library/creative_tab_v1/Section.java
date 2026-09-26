package org.mythic_goose.amalgamation.library.creative_tab_v1;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;

public interface Section {
    String id();
    Component title();
    int textColor();
    List<Item> items();

    /**
     * Whether this section gets a banner row (blank spacer + title/texture) in the grid.
     * {@code false} for header-less sections like {@link SectionNone}, which sit flush
     * with no reserved row and are skipped entirely by {@link BannerRenderer}.
     */
    default boolean hasHeader() {
        return true;
    }
}
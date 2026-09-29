package org.mythic_goose.amalgamation.api.creative_tab_v1;

import net.minecraft.world.level.ItemLike;

/** Receiver passed into a {@code displaySection} block; call {@code add} for each item. */
@FunctionalInterface
public interface SectionOutput {

    void add(ItemLike item);

    default void add(ItemLike... items) {
        for (ItemLike item : items) {
            add(item);
        }
    }
}
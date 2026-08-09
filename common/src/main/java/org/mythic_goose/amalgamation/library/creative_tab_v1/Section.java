package org.mythic_goose.amalgamation.library.creative_tab_v1;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;

public interface Section {
    String id();
    Component title();
    int textColor();
    List<Item> items();
}
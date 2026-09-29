package org.mythic_goose.amalgamation.api.creative_tab_v1;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;

/**
 * Describes how a section's banner should be drawn. Pass one of the factory methods
 * below as the {@code sectionType} argument of {@link SectionTabBuilder#displaySection}.
 */
@FunctionalInterface
public interface SectionStyle {

    Section build(String modId, String sectionId, Component title, List<Item> items);

    /** A flat-colored banner. Text defaults to white. */
    static SectionStyle colored(int bannerColor) {
        return colored(bannerColor, 0xFFFFFFFF);
    }

    static SectionStyle colored(int bannerColor, int textColor) {
        return (modId, sectionId, title, items) ->
                new SectionColored(sectionId, title, bannerColor, textColor, items);
    }

    /**
     * A textured banner. The texture is resolved automatically to
     * {@code [modId]:textures/gui/tab_overlay/[sectionId].png}. Text defaults to white.
     */
    static SectionStyle textured() {
        return textured(0xFFFFFFFF);
    }

    static SectionStyle textured(int textColor) {
        return (modId, sectionId, title, items) ->
                SectionTextured.of(modId, sectionId, title, textColor, items);
    }

    /**
     * No banner at all - items sit flush with no header row. Used internally by
     * {@link SectionTabBuilder#emptySection}; title and text color are ignored.
     */
    static SectionStyle none() {
        return (modId, sectionId, title, items) -> new SectionNone(sectionId, items);
    }
}
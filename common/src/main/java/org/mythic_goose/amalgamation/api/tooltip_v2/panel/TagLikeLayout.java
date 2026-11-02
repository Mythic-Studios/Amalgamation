package org.mythic_goose.amalgamation.api.tooltip_v2.panel;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Card-style tooltip layout:
 *
 * <pre>
 * [icon]  Item Name
 *         tag (custom subtext Component)
 * ------------------------------
 * Description info (wrapped)
 * ------------------------------
 * MOD NAME
 * </pre>
 *
 * Everything except the tag is optional: name/icon default to the stack's own,
 * the mod name defaults to the item's registry namespace.
 */
public final class TagLikeLayout {
    private final Component tag;
    private Component name;          // null -> stack.getHoverName()
    private ItemStack icon;          // null -> the stack itself
    private Component modName;       // null -> derived from registry namespace
    private final List<Component> description = new ArrayList<>();
    private PanelDesign design;      // null -> PanelDesign.DEFAULT

    private TagLikeLayout(Component tag) {
        this.tag = tag;
    }

    /** Entry point: {@code TagLikeLayout.tagLike(Component.literal("Legendary Sword"))...} */
    public static TagLikeLayout tagLike(Component tag) {
        return new TagLikeLayout(tag);
    }

    public TagLikeLayout name(Component name) { this.name = name; return this; }
    public TagLikeLayout icon(ItemStack icon) { this.icon = icon; return this; }
    public TagLikeLayout modName(Component modName) { this.modName = modName; return this; }
    public TagLikeLayout modName(String modName) { this.modName = Component.literal(modName); return this; }
    public TagLikeLayout description(Component... lines) { this.description.addAll(List.of(lines)); return this; }
    public TagLikeLayout description(String text) { this.description.add(Component.literal(text)); return this; }
    /** Optional: also restyle the box (background/border) for this layout. */
    public TagLikeLayout panel(PanelDesign design) { this.design = design; return this; }

    public Component tag() { return tag; }
    public Component name() { return name; }
    public ItemStack icon() { return icon; }
    public Component modName() { return modName; }
    public List<Component> description() { return description; }
    public PanelDesign design() { return design != null ? design : PanelDesign.DEFAULT; }
}
package org.mythic_goose.amalgamation.api.tooltip_v2.panel;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.mythic_goose.amalgamation.api.tooltip_v2.colors.StyledText;
import org.mythic_goose.amalgamation.api.tooltip_v2.colors.StyledText.Glyph;
import org.mythic_goose.amalgamation.api.tooltip_v2.wrapping.WrappableTooltipComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Compact card layout (inspired by the "Oak Log" reference):
 *
 * <pre>
 * [icon]  Item Name
 *         tag
 * ------------------------------
 * description lines...
 * ------------------------------
 * MOD NAME
 * </pre>
 *
 * Only the icon gets a frame; sections are separated by thin dividers.
 */
public class TagPanelComponent implements WrappableTooltipComponent {
    private static final int ICON = 16;                 // real item size
    private static final int ICON_BOX = ICON + 4;       // 2px margin around the item -> 20x20
    private static final int TEXT_H = 9;                // font height
    private static final int LINE = 10;                 // description line height
    private static final int GAP = 4;                   // icon <-> text
    private static final int SECTION_GAP = 3;           // space above/below a divider
    private static final int MAX_NATURAL_DESC = 180;

    private final ItemStack stack;
    private final TagLikeLayout layout;
    private final int forcedWidth; // -1 = natural

    private final List<Glyph> nameG;
    private final List<Glyph> tagG;
    private final List<Glyph> modG;
    private final List<Glyph> descG;

    private Font cachedFont;
    private int cachedW = -1;
    private List<List<Glyph>> descLines = List.of();

    public TagPanelComponent(ItemStack stack, TagLikeLayout layout) {
        this(stack, layout, -1);
    }

    private TagPanelComponent(ItemStack stack, TagLikeLayout layout, int forcedWidth) {
        this.stack = stack;
        this.layout = layout;
        this.forcedWidth = forcedWidth;

        Component name = layout.name() != null ? layout.name() : stack.getHoverName();
        this.nameG = StyledText.glyphs(name.getString(), baseColor(name, 0xFFFFFFFF));
        this.tagG = StyledText.glyphs(layout.tag().getString(), baseColor(layout.tag(), 0xFFAAAAAA));

        Component mod = layout.modName();
        String modText = mod != null ? mod.getString()
                : BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().replace('_', ' ').toUpperCase(Locale.ROOT);
        this.modG = StyledText.glyphs(modText, mod != null ? baseColor(mod, 0xFF7777FF) : 0xFF7777FF);

        List<Glyph> desc = new ArrayList<>();
        List<Component> lines = layout.description();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) desc.add(new Glyph('\n', null, 0, 1, 0));
            desc.addAll(StyledText.glyphs(lines.get(i).getString(), baseColor(lines.get(i), 0xFFDDDDDD)));
        }
        this.descG = desc;
    }

    private static int baseColor(Component c, int fallback) {
        return c.getStyle().getColor() != null ? 0xFF000000 | c.getStyle().getColor().getValue() : fallback;
    }

    // ---- sizing -------------------------------------------------------

    private int naturalWidth(Font font) {
        int header = ICON_BOX + GAP + Math.max(StyledText.width(font, nameG), StyledText.width(font, tagG));
        int desc = 0;
        for (List<Glyph> l : StyledText.wrap(font, descG, Integer.MAX_VALUE)) {
            desc = Math.max(desc, StyledText.width(font, l));
        }
        desc = Math.min(desc, MAX_NATURAL_DESC);
        int mod = StyledText.width(font, modG);
        return Math.max(header, Math.max(desc, mod));
    }

    private int resolveWidth(Font font) {
        int natural = naturalWidth(font);
        if (forcedWidth <= 0) return natural;
        return Math.min(natural, Math.max(forcedWidth, ICON_BOX + GAP + 40));
    }

    private void layoutLines(Font font) {
        int w = resolveWidth(font);
        if (font == cachedFont && w == cachedW) return;
        descLines = descG.isEmpty() ? List.of() : StyledText.wrap(font, descG, w);
        cachedFont = font;
        cachedW = w;
    }

    @Override
    public int getWidth(Font font) {
        return resolveWidth(font);
    }

    @Override
    public int getHeight(Font font) {
        layoutLines(font);
        int h = ICON_BOX;
        if (!descLines.isEmpty()) h += SECTION_GAP * 2 + 1 + descLines.size() * LINE - (LINE - TEXT_H);
        h += SECTION_GAP * 2 + 1 + TEXT_H;
        return h;
    }

    @Override
    public List<ClientTooltipComponent> wrapTo(Font font, int maxWidth) {
        if (naturalWidth(font) <= maxWidth) return List.of(this);
        return List.of(new TagPanelComponent(stack, layout, maxWidth));
    }

    // ---- drawing ------------------------------------------------------

    private static void outline(GuiGraphicsExtractor g, int x, int y, int w, int h, int color) {
        g.fill(x, y, x + w, y + 1, color);
        g.fill(x, y + h - 1, x + w, y + h, color);
        g.fill(x, y, x + 1, y + h, color);
        g.fill(x + w - 1, y, x + w, y + h, color);
    }

    @Override
    public void extractText(GuiGraphicsExtractor g, Font font, int x, int y) {
        layoutLines(font);
        int w = cachedW;
        long now = System.currentTimeMillis();
        int accent = layout.design().borderTop().argb(now) | 0xFF000000;
        int divider = (accent & 0x00FFFFFF) | 0x99000000;

        // Icon frame + real 16x16 item
        g.fill(x, y, x + ICON_BOX, y + ICON_BOX, 0x66000000);
        outline(g, x, y, ICON_BOX, ICON_BOX, accent);
        ItemStack icon = layout.icon() != null ? layout.icon() : stack;
        g.item(icon, x + 2, y + 2); // confirm method name in your GuiGraphicsExtractor

        // Name + tag, centred against the icon frame
        int tx = x + ICON_BOX + GAP;
        int blockH = TEXT_H * 2 + 1;
        int ty = y + (ICON_BOX - blockH) / 2;
        StyledText.draw(g, font, nameG, tx, ty, now);
        StyledText.draw(g, font, tagG, tx, ty + TEXT_H + 1, now);

        int cy = y + ICON_BOX;

        // Description
        if (!descLines.isEmpty()) {
            cy += SECTION_GAP;
            g.fill(x, cy, x + w, cy + 1, divider);
            cy += 1 + SECTION_GAP;
            for (List<Glyph> line : descLines) {
                StyledText.draw(g, font, line, x, cy, now);
                cy += LINE;
            }
            cy -= (LINE - TEXT_H);
        }

        // Mod name
        cy += SECTION_GAP;
        g.fill(x, cy, x + w, cy + 1, divider);
        cy += 1 + SECTION_GAP;
        StyledText.draw(g, font, modG, x, cy, now);
    }
}
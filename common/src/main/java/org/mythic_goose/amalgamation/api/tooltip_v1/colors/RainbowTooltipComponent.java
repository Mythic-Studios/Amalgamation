package org.mythic_goose.amalgamation.api.tooltip_v1.colors;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.mythic_goose.amalgamation.api.tooltip_v1.wrapping.WrappableTooltipComponent;

import java.util.ArrayList;
import java.util.List;

public class RainbowTooltipComponent implements WrappableTooltipComponent {

    /** One character, remembering its segment and index so the gradient stays continuous across wrapped lines. */
    private record Glyph(TooltipSegment seg, int index) {
        char ch() {
            return seg.text().charAt(index);
        }
    }

    private final List<Glyph> glyphs;

    public RainbowTooltipComponent(List<TooltipSegment> segments) {
        List<Glyph> list = new ArrayList<>();
        for (TooltipSegment s : segments) {
            for (int i = 0; i < s.text().length(); i++) {
                list.add(new Glyph(s, i));
            }
        }
        this.glyphs = list;
    }

    private RainbowTooltipComponent(List<Glyph> glyphs, boolean internal) {
        this.glyphs = glyphs;
    }

    private int widthOf(Font font, int from, int to) {
        int w = 0;
        for (int i = from; i < to; i++) {
            w += font.width(String.valueOf(glyphs.get(i).ch()));
        }
        return w;
    }

    @Override
    public int getWidth(Font font) {
        return widthOf(font, 0, glyphs.size());
    }

    @Override
    public int getHeight(Font font) {
        return 9;
    }

    @Override
    public List<ClientTooltipComponent> wrapTo(Font font, int maxWidth) {
        if (getWidth(font) <= maxWidth) {
            return List.of(this);
        }

        List<ClientTooltipComponent> out = new ArrayList<>();
        int start = 0;
        int width = 0;
        int lastSpace = -1;

        for (int i = 0; i < glyphs.size(); i++) {
            char c = glyphs.get(i).ch();
            if (c == ' ') lastSpace = i;
            width += font.width(String.valueOf(c));

            if (width > maxWidth && i > start) {
                boolean atSpace = lastSpace > start;
                int end = atSpace ? lastSpace : i; // exclusive
                out.add(new RainbowTooltipComponent(new ArrayList<>(glyphs.subList(start, end)), true));
                start = atSpace ? lastSpace + 1 : i;
                lastSpace = -1;
                width = widthOf(font, start, i + 1);
            }
        }

        if (start < glyphs.size()) {
            out.add(new RainbowTooltipComponent(new ArrayList<>(glyphs.subList(start, glyphs.size())), true));
        }
        return out;
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        long now = System.currentTimeMillis();
        int cursorX = x;
        for (Glyph g : glyphs) {
            String ch = String.valueOf(g.ch());
            int color = g.seg().style() != null
                    ? g.seg().style().color(g.index(), g.seg().text().length(), now)
                    : 0xFFFFFFFF; // plain white after _n
            graphics.text(font, ch, cursorX, y, color, true);
            cursorX += font.width(ch);
        }
    }
}
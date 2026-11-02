package org.mythic_goose.amalgamation.api.tooltip_v2.colors;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

/** Glyph-level helper so ~r / ~l / ~n style markers work in any text, including wrapped text. */
public final class StyledText {
    private StyledText() {}

    public record Glyph(char ch, TooltipStyle style, int index, int length, int baseColor) {}

    /** Parses markers (~r, ~l, ~n ...) in {@code s} into glyphs. Text with no markers becomes plain glyphs. */
    public static List<Glyph> glyphs(String s, int baseColor) {
        List<TooltipSegment> segs = TooltipParser.parse(s);
        if (segs == null) segs = List.of(new TooltipSegment(s, null));
        List<Glyph> out = new ArrayList<>();
        for (TooltipSegment seg : segs) {
            String t = seg.text();
            for (int i = 0; i < t.length(); i++) {
                out.add(new Glyph(t.charAt(i), seg.style(), i, t.length(), baseColor));
            }
        }
        return out;
    }

    public static int width(Font font, List<Glyph> glyphs) {
        int w = 0;
        for (Glyph g : glyphs) w += font.width(String.valueOf(g.ch()));
        return w;
    }

    /** Word-wraps glyphs to maxWidth; '\n' forces a line break. */
    public static List<List<Glyph>> wrap(Font font, List<Glyph> glyphs, int maxWidth) {
        List<List<Glyph>> out = new ArrayList<>();
        List<Glyph> line = new ArrayList<>();
        int w = 0;
        int lastSpace = -1;

        for (Glyph g : glyphs) {
            if (g.ch() == '\n') {
                out.add(line);
                line = new ArrayList<>();
                w = 0;
                lastSpace = -1;
                continue;
            }
            int cw = font.width(String.valueOf(g.ch()));
            if (w + cw > maxWidth && !line.isEmpty()) {
                if (lastSpace >= 0) {
                    out.add(new ArrayList<>(line.subList(0, lastSpace)));
                    line = new ArrayList<>(line.subList(lastSpace + 1, line.size()));
                } else {
                    out.add(line);
                    line = new ArrayList<>();
                }
                w = width(font, line);
                lastSpace = -1;
                for (int i = 0; i < line.size(); i++) if (line.get(i).ch() == ' ') lastSpace = i;
            }
            if (g.ch() == ' ') lastSpace = line.size();
            line.add(g);
            w += cw;
        }
        out.add(line);
        return out;
    }

    public static void draw(GuiGraphicsExtractor graphics, Font font, List<Glyph> line, int x, int y, long now) {
        int cx = x;
        for (Glyph g : line) {
            String ch = String.valueOf(g.ch());
            int color = g.style() != null ? g.style().color(g.index(), g.length(), now) : g.baseColor();
            graphics.text(font, ch, cx, y, color, true);
            cx += font.width(ch);
        }
    }
}
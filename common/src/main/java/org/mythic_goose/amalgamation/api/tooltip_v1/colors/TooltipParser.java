package org.mythic_goose.amalgamation.api.tooltip_v1.colors;

import java.util.ArrayList;
import java.util.List;

public final class TooltipParser {
    private static final String END = "~n";

    private TooltipParser() {}

    /** Splits a line into styled/plain segments. Returns null if the line has no markers at all. */
    public static List<TooltipSegment> parse(String plain) {
        List<TooltipSegment> out = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        TooltipStyle current = null;
        boolean foundMarker = false;

        int i = 0;
        while (i < plain.length()) {
            if (plain.startsWith(END, i)) {
                flush(out, buf, current);
                current = null;
                foundMarker = true;
                i += END.length();
                continue;
            }
            TooltipStyle style = TooltipStyle.matchAt(plain, i);
            if (style != null) {
                flush(out, buf, current);
                current = style;
                foundMarker = true;
                i += TooltipStyle.MARKER_LENGTH;
                continue;
            }
            buf.append(plain.charAt(i));
            i++;
        }
        flush(out, buf, current);
        return foundMarker ? out : null;
    }

    private static void flush(List<TooltipSegment> out, StringBuilder buf, TooltipStyle style) {
        if (buf.length() > 0) {
            out.add(new TooltipSegment(buf.toString(), style));
            buf.setLength(0);
        }
    }
}
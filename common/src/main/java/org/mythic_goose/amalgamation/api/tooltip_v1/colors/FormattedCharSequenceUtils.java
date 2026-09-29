package org.mythic_goose.amalgamation.api.tooltip_v1.colors;

import net.minecraft.util.FormattedCharSequence;

public final class FormattedCharSequenceUtils {
    private FormattedCharSequenceUtils() {}

    public static String toPlainText(FormattedCharSequence seq) {
        StringBuilder sb = new StringBuilder();
        seq.accept((position, style, codepoint) -> {
            sb.appendCodePoint(codepoint);
            return true; // keep iterating
        });
        return sb.toString();
    }
}
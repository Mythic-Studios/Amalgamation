package org.mythic_goose.amalgamation.library.tooltip_v1;

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
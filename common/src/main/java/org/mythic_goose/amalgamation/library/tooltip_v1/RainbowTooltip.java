package org.mythic_goose.amalgamation.library.tooltip_v1;

import java.awt.*;

public final class RainbowTooltip {
    public static final String MARKER = "_z";

    public static boolean hasMarker(String plainText) {
        return plainText.contains(MARKER);
    }

    public static String stripMarker(String text) {
        return text.replace(MARKER, "");
    }

    public static int colorForIndex(int charIndex, long timeMillis) {
        float hue = ((charIndex * 0.05f) + (timeMillis % 2000L) / 2000f) % 1.0f;
        return 0xFF000000 | (Color.HSBtoRGB(hue, 0.9f, 1.0f) & 0xFFFFFF);
    }
}
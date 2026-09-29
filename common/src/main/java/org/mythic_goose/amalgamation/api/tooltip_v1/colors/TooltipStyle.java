package org.mythic_goose.amalgamation.api.tooltip_v1.colors;

public enum TooltipStyle {
    // animated rainbow
    RAINBOW("~r") {
        @Override
        public int color(int index, int length, long time) {
            float hue = ((index * 0.05f) + (time % 2000L) / 2000f) % 1.0f;
            return 0xFF000000 | (java.awt.Color.HSBtoRGB(hue, 0.9f, 1.0f) & 0xFFFFFF);
        }
    },

    // Pastel rainbow: same hue cycle as RAINBOW, but soft and shimmering
    FOIL("~f") {
        @Override
        public int color(int index, int length, long time) {
            float hue = ((index * 0.045f) + (time % 3000L) / 3000f) % 1.0f;

            // Saturation drifts slightly along the text and over time for a foil-like shimmer
            float shimmer = 0.5f + 0.5f * (float) Math.sin((index * 0.35f) - (time / 400f));
            float saturation = 0.30f + 0.15f * shimmer; // 0.30 - 0.45

            return 0xFF000000 | (java.awt.Color.HSBtoRGB(hue, saturation, 1.0f) & 0xFFFFFF);
        }
    },

    // dark red, slow pulse
    DARK("~d") {
        private static final int A = 0x7A0F1E; // deep red
        private static final int B = 0xD0243B; // brighter crimson (keeps it readable on the dark tooltip bg)

        @Override
        public int color(int index, int length, long time) {
            float t = wave(index * 0.05f - time / 3500f);
            return 0xFF000000 | lerp(A, B, t);
        }
    },

    // indigo <-> pink
    PURPLE("~a") {
        private static final int A = 0x5B6CFF; // indigo/blue
        private static final int B = 0xE0559F; // pink

        @Override
        public int color(int index, int length, long time) {
            float t = wave(index * 0.08f - time / 3000f);
            return 0xFF000000 | lerp(A, B, t);
        }
    };

    private final String marker;

    TooltipStyle(String marker) {
        this.marker = marker;
    }

    /** ARGB color for the character at {@code index} of a line that is {@code length} chars long. */
    public abstract int color(int index, int length, long timeMillis);

    public String strip(String plain) {
        return plain.substring(marker.length());
    }

    public static final int MARKER_LENGTH = 2;

    /** Returns the style whose marker sits at {@code offset} in the text, or null. */
    public static TooltipStyle matchAt(String plain, int offset) {
        for (TooltipStyle style : values()) {
            if (plain.startsWith(style.marker, offset)) return style;
        }
        return null;
    }

    // 0..1 smooth back-and-forth
    private static float wave(float phase) {
        return 0.5f + 0.5f * (float) Math.sin(phase * Math.PI * 2);
    }

    private static int lerp(int a, int b, float t) {
        int r = (int) (((a >> 16) & 0xFF) + ((((b >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * t));
        int g = (int) (((a >> 8) & 0xFF) + ((((b >> 8) & 0xFF) - ((a >> 8) & 0xFF)) * t));
        int bl = (int) ((a & 0xFF) + (((b & 0xFF) - (a & 0xFF)) * t));
        return (r << 16) | (g << 8) | bl;
    }
}

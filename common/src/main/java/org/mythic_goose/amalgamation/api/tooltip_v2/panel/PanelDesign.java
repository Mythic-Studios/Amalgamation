package org.mythic_goose.amalgamation.api.tooltip_v2.panel;

/**
 * Describes how the tooltip box looks: a vertical gradient for the background
 * and a vertical gradient for the 1px border. Every color is a {@link ColorSource}
 * so it can be static or animated.
 */
public record PanelDesign(ColorSource backgroundTop, ColorSource backgroundBottom,
                          ColorSource borderTop, ColorSource borderBottom) {

    /** Vanilla look. */
    public static final PanelDesign DEFAULT = gradient(0xF0100010, 0xF0100010, 0x505000FF, 0x5028007F);

    @FunctionalInterface
    public interface ColorSource {
        /** @return ARGB color at the given time. */
        int argb(long timeMillis);
    }

    // ---- color sources -------------------------------------------------

    public static ColorSource solid(int argb) {
        return t -> argb;
    }

    /** Smoothly fades a <-> b over {@code periodMillis}. */
    public static ColorSource pulse(int a, int b, long periodMillis) {
        return t -> {
            float phase = (t % periodMillis) / (float) periodMillis;
            float f = 0.5f + 0.5f * (float) Math.sin(phase * Math.PI * 2);
            return lerpArgb(a, b, f);
        };
    }

    /** Cycles through the hue wheel. */
    public static ColorSource rainbow(long periodMillis, float saturation, float brightness, int alpha) {
        return t -> {
            float hue = (t % periodMillis) / (float) periodMillis;
            return (alpha << 24) | (java.awt.Color.HSBtoRGB(hue, saturation, brightness) & 0xFFFFFF);
        };
    }

    // ---- factories -----------------------------------------------------

    public static PanelDesign of(int background, int border) {
        return new PanelDesign(solid(background), solid(background), solid(border), solid(border));
    }

    public static PanelDesign gradient(int bgTop, int bgBottom, int borderTop, int borderBottom) {
        return new PanelDesign(solid(bgTop), solid(bgBottom), solid(borderTop), solid(borderBottom));
    }

    public static PanelDesign animatedBorder(int background, int borderA, int borderB, long periodMillis) {
        ColorSource b = pulse(borderA, borderB, periodMillis);
        return new PanelDesign(solid(background), solid(background), b, b);
    }

    // ---- util ----------------------------------------------------------

    public static int lerpArgb(int a, int b, float t) {
        int aa = (int) (((a >>> 24) & 0xFF) + ((((b >>> 24) & 0xFF) - ((a >>> 24) & 0xFF)) * t));
        int r = (int) (((a >> 16) & 0xFF) + ((((b >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * t));
        int g = (int) (((a >> 8) & 0xFF) + ((((b >> 8) & 0xFF) - ((a >> 8) & 0xFF)) * t));
        int bl = (int) ((a & 0xFF) + (((b & 0xFF) - (a & 0xFF)) * t));
        return (aa << 24) | (r << 16) | (g << 8) | bl;
    }
}
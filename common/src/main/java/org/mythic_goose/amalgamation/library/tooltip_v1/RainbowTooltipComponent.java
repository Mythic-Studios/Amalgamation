package org.mythic_goose.amalgamation.library.tooltip_v1;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;


public class RainbowTooltipComponent implements ClientTooltipComponent {
    private final String text;

    public RainbowTooltipComponent(String plainTextWithoutMarker) {
        this.text = plainTextWithoutMarker;
    }

    @Override
    public int getWidth(Font font) {
        return font.width(text);
    }

    @Override
    public int getHeight(Font font) {
        return 9; // matches Font.lineHeight
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        long now = System.currentTimeMillis();
        int cursorX = x;
        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            int color = RainbowTooltip.colorForIndex(i, now);
            graphics.text(font, ch, cursorX, y, color, true); // true = drop shadow, matches vanilla tooltip text
            cursorX += font.width(ch);
        }
    }
}
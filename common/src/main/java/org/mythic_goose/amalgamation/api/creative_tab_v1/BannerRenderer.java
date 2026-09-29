package org.mythic_goose.amalgamation.api.creative_tab_v1;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import org.mythic_goose.amalgamation.mixin.accessor.CreativeModeInventoryScreenAccessor;

public class BannerRenderer {

    private static final int ROW_HEIGHT    = 18;
    private static final int GRID_COLS     = 9;
    private static final int GRID_X_OFFSET = 10;
    private static final int GRID_Y_OFFSET = 17;

    public static void render(CreativeModeInventoryScreen screen, GuiGraphicsExtractor graphics, TabLayout layout, int currentRow) {
        CreativeModeInventoryScreenAccessor accessor =
                (CreativeModeInventoryScreenAccessor)(AbstractContainerScreen<?>) screen;

        int left = accessor.amalgamation$getLeftPos() + GRID_X_OFFSET;
        int top  = accessor.amalgamation$getTopPos()  + GRID_Y_OFFSET;
        int w    = GRID_COLS * ROW_HEIGHT - 4;

        Font font = Minecraft.getInstance().font;

        for (Section section : layout.sections()) {
            int sectionRow = layout.rowOf(section.id());
            if (sectionRow < 0) continue;

            int relativeRow = sectionRow - currentRow;
            if (relativeRow < 0 || relativeRow >= 5) continue;

            int x = left;
            int y = top + relativeRow * ROW_HEIGHT;
            int h = ROW_HEIGHT - 1;

            if (section instanceof SectionColored colored) {
                graphics.fill(x - 1, y, x + w + 1, y + h, colored.bannerColor());
                graphics.fill(x - 1, y, x + w + 1, y + 1, brighten(colored.bannerColor(), 0.4f));
            } else if (section instanceof SectionTextured textured) {
            int texWidth = 158;
            int texHeight = 16;
            graphics.blit(RenderPipelines.GUI_TEXTURED, textured.texture(), x - 1, y, 0, 0, w + 2, h, texWidth, texHeight);
        }

            int textX = x + 4;
            int textY = y + (h - font.lineHeight) / 2;
            graphics.text(font, section.title(), textX, textY, section.textColor(), true);
        }
    }

    private static int brighten(int argb, float amount) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >>  8) & 0xFF;
        int b =  argb        & 0xFF;
        r = (int)(r + (255 - r) * amount);
        g = (int)(g + (255 - g) * amount);
        b = (int)(b + (255 - b) * amount);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
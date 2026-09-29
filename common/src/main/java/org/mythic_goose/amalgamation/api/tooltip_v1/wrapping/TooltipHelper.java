package org.mythic_goose.amalgamation.api.tooltip_v1.wrapping;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.mythic_goose.amalgamation.mixin.accessor.OrderedTextToolTipAccessor;

import java.util.List;

public class TooltipHelper {

    private static boolean shouldFlip = false;

    public static void newFix(List<ClientTooltipComponent> components, Font font, int x, int width) {
        shouldFlip = false;

        // Widest component that cannot wrap itself
        int forcedWidth = 0;
        for (ClientTooltipComponent component : components) {
            if (!(component instanceof ClientTextTooltip) && !(component instanceof WrappableTooltipComponent)) {
                forcedWidth = Math.max(forcedWidth, component.getWidth(font));
            }
        }

        int maxWidth = width - 20 - x;
        if (forcedWidth > maxWidth || maxWidth < 100) {
            shouldFlip = true;
            maxWidth = x - 28;
        }
        maxWidth = Math.max(maxWidth, 40);

        wrapCustomComponents(components, font, maxWidth);
        wrapNewLines(components);
        wrapLongLines(components, font, maxWidth);
    }

    public static int shouldFlip(List<ClientTooltipComponent> components, Font font, int x) {
        int maxWidth = 0;
        for (ClientTooltipComponent tooltipComponent : components) {
            maxWidth = Math.max(maxWidth, tooltipComponent.getWidth(font));
        }
        int renderX = x + 12;

        if (shouldFlip) {
            renderX -= 28 + maxWidth;
        }

        return Math.max(4, renderX);
    }

    private static void wrapCustomComponents(List<ClientTooltipComponent> components, Font font, int maxWidth) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i) instanceof WrappableTooltipComponent wrappable) {
                List<ClientTooltipComponent> parts = wrappable.wrapTo(font, maxWidth);
                components.remove(i);
                components.addAll(i, parts);
                i += parts.size() - 1;
            }
        }
    }

    private static void wrapLongLines(List<ClientTooltipComponent> components, Font font, int maxSize) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i) instanceof ClientTextTooltip clientTextTooltip) {
                Component text = OrderedTextToTextVisitor.get(((OrderedTextToolTipAccessor) clientTextTooltip).getText());
                if (text.getSiblings().isEmpty()) continue;

                List<ClientTooltipComponent> wrapped = font.split(text, maxSize).stream().map(ClientTooltipComponent::create).toList();
                components.remove(i);
                components.addAll(i, wrapped);
                i += wrapped.size() - 1;
            }
        }
    }

    private static void wrapNewLines(List<ClientTooltipComponent> components) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i) instanceof ClientTextTooltip clientTextTooltip) {
                Component text = OrderedTextToTextVisitor.get(((OrderedTextToolTipAccessor) clientTextTooltip).getText());

                List<Component> children = text.getSiblings();
                for (int j = 0; j < children.size() - 1; j++) {
                    String code = children.get(j).getString() + children.get(j + 1).getString();
                    if (code.equals("\\n")) {
                        components.set(i, ClientTooltipComponent.create(textWithChildren(children, 0, j).getVisualOrderText()));
                        components.add(i + 1, ClientTooltipComponent.create(textWithChildren(children, j + 2, children.size()).getVisualOrderText()));
                        break;
                    }
                }
            }
        }
    }

    private static Component textWithChildren(List<Component> children, int from, int end) {
        MutableComponent text = Component.literal("");
        for (int i = from; i < end; i++) {
            text.append(children.get(i));
        }
        return text;
    }
}
package org.mythic_goose.amalgamation.library.creative_tab_v1;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the section layout for a single creative tab.
 * <p>
 * Each {@link CreativeModeTab} built through {@link SectionTabBuilder} gets its own
 * {@code TabLayout} instance, so section ids and row positions never collide between tabs.
 */
public class TabLayout {

    private static final Map<CreativeModeTab, TabLayout> BY_TAB = new IdentityHashMap<>();

    private final List<Section> sections;
    private final Map<String, Integer> sectionRow = new HashMap<>();

    private TabLayout(List<Section> sections) {
        this.sections = sections;
    }

    /** Registers the layout for a tab. Called once, from {@link SectionTabBuilder#build()}. */
    static void register(CreativeModeTab tab, List<Section> sections) {
        BY_TAB.put(tab, new TabLayout(List.copyOf(sections)));
    }

    /** Returns the layout registered for {@code tab}, or {@code null} if it isn't a section-based tab. */
    public static TabLayout forTab(CreativeModeTab tab) {
        return BY_TAB.get(tab);
    }

    /** The sections that make up this tab, in display order. */
    public List<Section> sections() {
        return sections;
    }

    /** The row a section's banner starts on, or -1 if unknown (layout hasn't been built yet). */
    public int rowOf(String sectionId) {
        return sectionRow.getOrDefault(sectionId, -1);
    }

    /**
     * Builds the flattened {@link ItemStack} grid for this tab (one blank row per section
     * header, followed by its items padded out to a full row), recording each section's
     * starting row along the way for {@link BannerRenderer} to use.
     */
    public List<ItemStack> build() {
        sectionRow.clear();
        List<ItemStack> result = new ArrayList<>();
        int row = 0;

        for (Section section : sections) {
            if (section.hasHeader()) {
                sectionRow.put(section.id(), row);
                for (int i = 0; i < 9; i++) {
                    result.add(ItemStack.EMPTY);
                }
                row++;
            }

            List<ItemStack> stacks = section.items().stream()
                    .map(ItemStack::new)
                    .toList();
            result.addAll(stacks);

            int itemCount = stacks.size();
            int usedInLastRow = itemCount % 9;
            if (usedInLastRow != 0) {
                int padding = 9 - usedInLastRow;
                for (int i = 0; i < padding; i++) {
                    result.add(ItemStack.EMPTY);
                }
                row += (itemCount / 9) + 1;
            } else {
                row += itemCount / 9;
            }
        }

        return result;
    }
}
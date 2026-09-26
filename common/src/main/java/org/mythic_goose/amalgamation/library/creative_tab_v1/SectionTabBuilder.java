package org.mythic_goose.amalgamation.library.creative_tab_v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Fluent builder for a section-based creative tab.
 *
 * <pre>{@code
 * SectionTabBuilder builder = SectionTabBuilder.create(
 *         Identifier.fromNamespaceAndPath(Constants.MOD_ID, "amalgamation_tab")) // auto-placed
 *     .icon(AmalgamationItems.RUBY_CRYSTAL)
 *     .title(Component.translatable("itemGroup.amalgamation_tab"))
 *     .emptySection(output -> {
 *         output.add(AmalgamationItems.RUBY_NUGGET);
 *     })
 *     .displaySection("crystals", SectionStyle.colored(0xFF97119f), output -> {
 *         output.add(AmalgamationItems.RUBY_DUST);
 *         output.add(AmalgamationItems.RUBY_CRYSTAL);
 *         output.add(AmalgamationItems.RUBY_INGOT);
 *         output.add(AmalgamationItems.RUBY_BLOCK);
 *     })
 *     .displaySection("tools", SectionStyle.textured(), output -> {
 *         output.add(AmalgamationItems.RUBY_PICKAXE);
 *     });
 *
 * // Fabric:
 * public static final CreativeModeTab AMALGAMATION_TAB =
 *         Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, builder.id(), builder.build());
 *
 * // NeoForge - via DeferredRegister, during the registration event:
 * private static final DeferredRegister<CreativeModeTab> TABS =
 *         DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExampleMod.MOD_ID);
 * public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AMALGAMATION_TAB =
 *         TABS.register("amalgamation_tab", () -> builder.build());
 * }</pre>
 *
 * {@code build()} only constructs the tab and wires up its sections - registering it into
 * the game is left to you, since that step differs per loader (see {@link #build()}).
 */
public class SectionTabBuilder {

    private final Identifier id;
    private final String modId;
    private final CreativeModeTab.Builder delegate;
    private final List<Section> sections = new ArrayList<>();
    private List<Item> emptySectionItems;

    private SectionTabBuilder(Identifier id, CreativeModeTab.Row row, int column) {
        this.id = id;
        this.modId = id.getNamespace();
        this.delegate = CreativeModeTab.builder(row, column);
    }

    /** Auto-picks a free column in the top row (see {@link #create(Identifier, CreativeModeTab.Row)}). */
    public static SectionTabBuilder create(Identifier id) {
        return create(id, CreativeModeTab.Row.TOP);
    }

    /** Auto-picks the first column in {@code row} not already used by a registered tab. */
    public static SectionTabBuilder create(Identifier id, CreativeModeTab.Row row) {
        return create(id, row, nextFreeColumn(row));
    }

    /**
     * Explicit placement, if you need to override auto-placement (see {@link #create(Identifier)}
     * and {@link #create(Identifier, CreativeModeTab.Row)}) - e.g. to force a specific slot,
     * or because auto-placement picked a column that's already taken by a tab registered
     * *after* yours (auto-placement only sees what's registered so far).
     *
     * @param row    which toolbar row the tab's button sits in
     * @param column which slot within that row
     */
    public static SectionTabBuilder create(Identifier id, CreativeModeTab.Row row, int column) {
        return new SectionTabBuilder(id, row, column);
    }

    /** The id this tab will be built with - use this to register the built tab on your loader. */
    public Identifier id() {
        return id;
    }

    private static int nextFreeColumn(CreativeModeTab.Row row) {
        Set<Integer> used = new HashSet<>();
        for (CreativeModeTab tab : BuiltInRegistries.CREATIVE_MODE_TAB) {
            if (tab.row() == row) {
                used.add(tab.column());
            }
        }
        int column = 0;
        while (used.contains(column)) {
            column++;
        }
        return column;
    }

    public SectionTabBuilder icon(ItemLike icon) {
        delegate.icon(() -> new ItemStack(icon));
        return this;
    }

    public SectionTabBuilder title(Component title) {
        delegate.title(title);
        return this;
    }

    /**
     * Adds a header-less block of items pinned to the very top of the tab, exactly like
     * vanilla's own creative inventory. Always placed first regardless of call order
     * relative to {@link #displaySection}. Can be called at most once per tab.
     *
     * @throws IllegalStateException if called more than once on the same builder
     */
    public SectionTabBuilder emptySection(Consumer<SectionOutput> output) {
        if (emptySectionItems != null) {
            throw new IllegalStateException("emptySection() can only be called once per tab");
        }
        List<Item> collected = new ArrayList<>();
        SectionOutput sink = item -> collected.add(item.asItem());
        output.accept(sink);
        emptySectionItems = collected;
        return this;
    }

    /**
     * Adds a section to this tab.
     *
     * @param sectionId unique id within this tab. Also used to derive the section's default
     *                   title ({@code itemGroup.[modId].[sectionId]}) and, for textured
     *                   sections, its texture path.
     * @param sectionType {@link SectionStyle#colored} or {@link SectionStyle#textured}
     * @param output      called once, immediately, with a receiver to {@code add} items to
     */
    public SectionTabBuilder displaySection(String sectionId, SectionStyle sectionType, Consumer<SectionOutput> output) {
        List<Item> collected = new ArrayList<>();
        SectionOutput sink = item -> collected.add(item.asItem());
        output.accept(sink);

        Component title = Component.translatable("itemGroup." + modId + "." + sectionId);
        sections.add(sectionType.build(modId, sectionId, title, collected));
        return this;
    }

    /**
     * Builds the tab and wires up its {@link TabLayout}. This does NOT register the tab
     * into {@code BuiltInRegistries.CREATIVE_MODE_TAB} - do that yourself, the way your
     * loader expects (see the class doc above).
     */
    public CreativeModeTab build() {
        List<Section> ordered = sections;
        if (emptySectionItems != null) {
            ordered = new ArrayList<>(sections.size() + 1);
            ordered.add(SectionStyle.none().build(modId, "empty", Component.empty(), emptySectionItems));
            ordered.addAll(sections);
        }

        CreativeModeTab tab = delegate.build();
        TabLayout.register(tab, ordered);
        return tab;
    }
}
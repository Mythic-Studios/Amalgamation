package org.mythic_goose.amalgamation.testmod;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import org.mythic_goose.amalgamation.library.creative_tab_v1.SectionStyle;
import org.mythic_goose.amalgamation.library.creative_tab_v1.SectionTabBuilder;

public class TestCreativeTab {

    public static CreativeModeTab TEST_TAB;

    /** Call once from the testmod's entrypoint, after TestBlocks/TestItems are registered. */
    public static void registerTestTab() {
        SectionTabBuilder builder = SectionTabBuilder.create(
                        Identifier.fromNamespaceAndPath(AmalgamationTestMod.MOD_ID, "test_tab"))
                .icon(TestItems.TEST_RUBY.get())
                .title(Component.literal("Amalgamation Test"))
                .displaySection("colored_section", SectionStyle.colored(0xFF97119f), output -> {
                    output.add(TestBlocks.TEST_ORE.get());
                    output.add(TestItems.TEST_RUBY.get());
                    output.add(TestItems.TEST_ORE_ITEM.get());
                })
                .displaySection("textured_section", SectionStyle.textured(), output -> {
                    output.add(TestItems.TEST_RUBY.get());
                });

        TEST_TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, builder.id(), builder.build());
    }
}
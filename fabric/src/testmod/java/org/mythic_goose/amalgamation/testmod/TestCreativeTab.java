package org.mythic_goose.amalgamation.testmod;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import org.mythic_goose.amalgamation.api.creative_tab_v1.CreativeTabRegistrar;
import org.mythic_goose.amalgamation.api.creative_tab_v1.SectionStyle;
import org.mythic_goose.amalgamation.api.creative_tab_v1.SectionTabBuilder;

import java.util.function.Supplier;

public class TestCreativeTab {


    public static Supplier<CreativeModeTab> TEST_TAB;

    public static void init(CreativeTabRegistrar registrar) {
        TEST_TAB = registrar.register("bw_tab", TestCreativeTab::buildTab);
    }

    private static CreativeModeTab buildTab() {
        SectionTabBuilder builder = SectionTabBuilder.create(
                        Identifier.fromNamespaceAndPath(AmalgamationTestMod.MOD_ID, "test_tab"))
                .icon(Items.SNOWBALL)
                .title(Component.translatable("itemGroup.bw_tab"))
                .displaySection("current", SectionStyle.none(), output -> {
                    output.add(TestItems.TEST_RUBY.get());
                    output.add(TestBlocks.TEST_ORE.get());
                });

        return builder.build();
    }
}
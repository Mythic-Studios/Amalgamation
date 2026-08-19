package org.mythic_goose.amalgamation.testmod;

import net.fabricmc.api.ModInitializer;
import org.mythic_goose.amalgamation.library.registry_v1.DeferredRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmalgamationTestMod implements ModInitializer {

    public static final String MOD_ID = "amalgamation-testmod";
    public static final Logger LOG = LoggerFactory.getLogger("AmalgamationTestMod");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Amalgamation testmod");

        DeferredRegister.setModId(MOD_ID);

        TestBlocks.registerTestBlocks();
        TestItems.registerTestItems();
        TestCreativeTab.registerTestTab();
    }
}
package org.mythic_goose.amalgamation.library.datagen_v1;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.mythic_goose.amalgamation.Constants;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class NeoForgeDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        AmalgamationDataGenerators.bootstrap();
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var registries = event.getLookupProvider();

        boolean isClient = event instanceof GatherDataEvent.Client;
        boolean isServer = event instanceof GatherDataEvent.Server;

        for (var entry : AmalgamationDataGenerators.getAll()) {
            boolean shouldRun = (entry.client() && isClient) || (entry.server() && isServer);
            if (shouldRun) {
                generator.addProvider(true, entry.factory().create(output, registries));
            }
        }
    }
}
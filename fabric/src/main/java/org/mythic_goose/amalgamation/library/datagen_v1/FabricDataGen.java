package org.mythic_goose.amalgamation.library.datagen_v1;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class FabricDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        AmalgamationDataGenerators.bootstrap();
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        for (var entry : AmalgamationDataGenerators.getAll()) {
            pack.addProvider((output, registriesFuture) ->
                    entry.factory().create(output, registriesFuture));
        }
    }
}

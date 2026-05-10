package com.styenvy.egtools.data;

import com.styenvy.egtools.EgTools;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public final class EgToolsDataGenerators {
    private EgToolsDataGenerators() {}

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new EgToolsRecipeProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new EgToolsItemTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeClient(), new EgToolsItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new EgToolsLanguageProvider(output));
    }
}

package com.styenvy.egtools.data;

import com.styenvy.egtools.EgTools;
import com.styenvy.egtools.EgToolsItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EgToolsItemModelProvider extends ItemModelProvider {
    public EgToolsItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EgTools.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(EgToolsItems.EMPTY_PAINT_BUCKET.get());
        paintBucket(EgToolsItems.WHITE_PAINT_BUCKET);
        paintBucket(EgToolsItems.LIGHT_GRAY_PAINT_BUCKET);
        paintBucket(EgToolsItems.GRAY_PAINT_BUCKET);
        paintBucket(EgToolsItems.BLACK_PAINT_BUCKET);
        paintBucket(EgToolsItems.BROWN_PAINT_BUCKET);
        paintBucket(EgToolsItems.RED_PAINT_BUCKET);
        paintBucket(EgToolsItems.ORANGE_PAINT_BUCKET);
        paintBucket(EgToolsItems.YELLOW_PAINT_BUCKET);
        paintBucket(EgToolsItems.LIME_PAINT_BUCKET);
        paintBucket(EgToolsItems.GREEN_PAINT_BUCKET);
        paintBucket(EgToolsItems.CYAN_PAINT_BUCKET);
        paintBucket(EgToolsItems.LIGHT_BLUE_PAINT_BUCKET);
        paintBucket(EgToolsItems.BLUE_PAINT_BUCKET);
        paintBucket(EgToolsItems.PURPLE_PAINT_BUCKET);
        paintBucket(EgToolsItems.MAGENTA_PAINT_BUCKET);
        paintBucket(EgToolsItems.PINK_PAINT_BUCKET);
    }

    private void paintBucket(DeferredHolder<Item, ? extends Item> bucket) {
        basicItem(bucket.get());
    }
}

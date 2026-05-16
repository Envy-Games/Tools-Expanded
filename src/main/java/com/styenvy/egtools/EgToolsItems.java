package com.styenvy.egtools;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EgToolsItems {
    private EgToolsItems() {}

    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(EgTools.MODID);

    public static final DeferredItem<PaxelItem> IRON_PAXEL = REGISTER.registerItem("iron_paxel",
            props -> new PaxelItem(Tiers.IRON, props), new Item.Properties().stacksTo(1));

    public static final DeferredItem<PaxelItem> DIAMOND_PAXEL = REGISTER.registerItem("diamond_paxel",
            props -> new PaxelItem(Tiers.DIAMOND, props), new Item.Properties().stacksTo(1));

    public static final DeferredItem<PaxelItem> NETHERITE_PAXEL = REGISTER.registerItem("netherite_paxel",
            props -> new PaxelItem(Tiers.NETHERITE, props), new Item.Properties().stacksTo(1).fireResistant());

    public static final DeferredItem<ConstructionHammerItem> CONSTRUCTION_HAMMER = REGISTER.registerItem("construction_hammer",
            ConstructionHammerItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<PaintBrushItem> PAINT_BRUSH = REGISTER.registerItem("paint_brush",
            PaintBrushItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> EMPTY_PAINT_BUCKET =
            REGISTER.registerSimpleItem("empty_paint_bucket", new Item.Properties().stacksTo(64));

    public static final DeferredItem<PaintBucketItem> WHITE_PAINT_BUCKET = paintBucket("white_paint_bucket", DyeColor.WHITE);
    public static final DeferredItem<PaintBucketItem> LIGHT_GRAY_PAINT_BUCKET = paintBucket("light_gray_paint_bucket", DyeColor.LIGHT_GRAY);
    public static final DeferredItem<PaintBucketItem> GRAY_PAINT_BUCKET = paintBucket("gray_paint_bucket", DyeColor.GRAY);
    public static final DeferredItem<PaintBucketItem> BLACK_PAINT_BUCKET = paintBucket("black_paint_bucket", DyeColor.BLACK);
    public static final DeferredItem<PaintBucketItem> BROWN_PAINT_BUCKET = paintBucket("brown_paint_bucket", DyeColor.BROWN);
    public static final DeferredItem<PaintBucketItem> RED_PAINT_BUCKET = paintBucket("red_paint_bucket", DyeColor.RED);
    public static final DeferredItem<PaintBucketItem> ORANGE_PAINT_BUCKET = paintBucket("orange_paint_bucket", DyeColor.ORANGE);
    public static final DeferredItem<PaintBucketItem> YELLOW_PAINT_BUCKET = paintBucket("yellow_paint_bucket", DyeColor.YELLOW);
    public static final DeferredItem<PaintBucketItem> LIME_PAINT_BUCKET = paintBucket("lime_paint_bucket", DyeColor.LIME);
    public static final DeferredItem<PaintBucketItem> GREEN_PAINT_BUCKET = paintBucket("green_paint_bucket", DyeColor.GREEN);
    public static final DeferredItem<PaintBucketItem> CYAN_PAINT_BUCKET = paintBucket("cyan_paint_bucket", DyeColor.CYAN);
    public static final DeferredItem<PaintBucketItem> LIGHT_BLUE_PAINT_BUCKET = paintBucket("light_blue_paint_bucket", DyeColor.LIGHT_BLUE);
    public static final DeferredItem<PaintBucketItem> BLUE_PAINT_BUCKET = paintBucket("blue_paint_bucket", DyeColor.BLUE);
    public static final DeferredItem<PaintBucketItem> PURPLE_PAINT_BUCKET = paintBucket("purple_paint_bucket", DyeColor.PURPLE);
    public static final DeferredItem<PaintBucketItem> MAGENTA_PAINT_BUCKET = paintBucket("magenta_paint_bucket", DyeColor.MAGENTA);
    public static final DeferredItem<PaintBucketItem> PINK_PAINT_BUCKET = paintBucket("pink_paint_bucket", DyeColor.PINK);

    private static DeferredItem<PaintBucketItem> paintBucket(String name, DyeColor color) {
        return REGISTER.registerItem(name, props -> new PaintBucketItem(props, color), new Item.Properties());
    }
}

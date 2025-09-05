package com.styenvy.egtools;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EgToolsItems {
    private EgToolsItems() {}

    public static final DeferredRegister<Item> REGISTER =
            DeferredRegister.create(Registries.ITEM, EgTools.MODID);

    // Iron Paxel: tiered multi-tool with iron stats
    public static final DeferredHolder<Item, PaxelItem> IRON_PAXEL = REGISTER.register("iron_paxel",
            () -> new PaxelItem(Tiers.IRON, new Item.Properties().stacksTo(1)));

    // Diamond Paxel: tiered multi-tool with diamond stats
    public static final DeferredHolder<Item, PaxelItem> DIAMOND_PAXEL = REGISTER.register("diamond_paxel",
            () -> new PaxelItem(Tiers.DIAMOND, new Item.Properties().stacksTo(1)));

    // Netherite Paxel: tiered multi-tool with netherite stats (fire-resistant like vanilla)
    public static final DeferredHolder<Item, PaxelItem> NETHERITE_PAXEL = REGISTER.register("netherite_paxel",
            () -> new PaxelItem(Tiers.NETHERITE, new Item.Properties().stacksTo(1).fireResistant()));

    // Construction Hammer: cycles through block variants when right-clicking
    public static final DeferredHolder<Item, ConstructionHammerItem> CONSTRUCTION_HAMMER = REGISTER.register("construction_hammer",
            () -> new ConstructionHammerItem(new Item.Properties().stacksTo(1)));

    // Paint Brush tool
    public static final DeferredHolder<Item, PaintBrushItem> PAINT_BRUSH = REGISTER.register("paint_brush",
            () -> new PaintBrushItem(new Item.Properties().stacksTo(1)));

    // Empty Paint Bucket
    public static final DeferredHolder<Item, Item> EMPTY_PAINT_BUCKET =
            REGISTER.register("empty_paint_bucket", () -> new Item(new Item.Properties().stacksTo(64)));

    // Colored Paint Buckets (32 uses each, stacks to 64)
    public static final DeferredHolder<Item, PaintBucketItem> WHITE_PAINT_BUCKET      =
            REGISTER.register("white_paint_bucket",      () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.WHITE));
    public static final DeferredHolder<Item, PaintBucketItem> LIGHT_GRAY_PAINT_BUCKET =
            REGISTER.register("light_gray_paint_bucket", () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.LIGHT_GRAY));
    public static final DeferredHolder<Item, PaintBucketItem> GRAY_PAINT_BUCKET       =
            REGISTER.register("gray_paint_bucket",       () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.GRAY));
    public static final DeferredHolder<Item, PaintBucketItem> BLACK_PAINT_BUCKET      =
            REGISTER.register("black_paint_bucket",      () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.BLACK));
    public static final DeferredHolder<Item, PaintBucketItem> BROWN_PAINT_BUCKET      =
            REGISTER.register("brown_paint_bucket",      () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.BROWN));
    public static final DeferredHolder<Item, PaintBucketItem> RED_PAINT_BUCKET        =
            REGISTER.register("red_paint_bucket",        () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.RED));
    public static final DeferredHolder<Item, PaintBucketItem> ORANGE_PAINT_BUCKET     =
            REGISTER.register("orange_paint_bucket",     () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.ORANGE));
    public static final DeferredHolder<Item, PaintBucketItem> YELLOW_PAINT_BUCKET     =
            REGISTER.register("yellow_paint_bucket",     () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.YELLOW));
    public static final DeferredHolder<Item, PaintBucketItem> LIME_PAINT_BUCKET       =
            REGISTER.register("lime_paint_bucket",       () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.LIME));
    public static final DeferredHolder<Item, PaintBucketItem> GREEN_PAINT_BUCKET      =
            REGISTER.register("green_paint_bucket",      () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.GREEN));
    public static final DeferredHolder<Item, PaintBucketItem> CYAN_PAINT_BUCKET       =
            REGISTER.register("cyan_paint_bucket",       () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.CYAN));
    public static final DeferredHolder<Item, PaintBucketItem> LIGHT_BLUE_PAINT_BUCKET =
            REGISTER.register("light_blue_paint_bucket", () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.LIGHT_BLUE));
    public static final DeferredHolder<Item, PaintBucketItem> BLUE_PAINT_BUCKET       =
            REGISTER.register("blue_paint_bucket",       () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.BLUE));
    public static final DeferredHolder<Item, PaintBucketItem> PURPLE_PAINT_BUCKET     =
            REGISTER.register("purple_paint_bucket",     () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.PURPLE));
    public static final DeferredHolder<Item, PaintBucketItem> MAGENTA_PAINT_BUCKET    =
            REGISTER.register("magenta_paint_bucket",    () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.MAGENTA));
    public static final DeferredHolder<Item, PaintBucketItem> PINK_PAINT_BUCKET       =
            REGISTER.register("pink_paint_bucket",       () -> new PaintBucketItem(new Item.Properties(), net.minecraft.world.item.DyeColor.PINK));
}
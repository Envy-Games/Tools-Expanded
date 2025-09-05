package com.styenvy.egtools;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EgToolsTabs {
    private EgToolsTabs() {}

    public static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EgTools.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EGTOOLS_TAB = REGISTER.register("egtools",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.egtools"))
                    .icon(() -> EgToolsItems.DIAMOND_PAXEL.get().getDefaultInstance())
                    .displayItems((params, out) -> {
                        // Tools
                        out.accept(EgToolsItems.IRON_PAXEL.get());
                        out.accept(EgToolsItems.DIAMOND_PAXEL.get());
                        out.accept(EgToolsItems.NETHERITE_PAXEL.get());
                        out.accept(EgToolsItems.CONSTRUCTION_HAMMER.get());
                        out.accept(EgToolsItems.PAINT_BRUSH.get());
                        // Empty bucket
                        out.accept(EgToolsItems.EMPTY_PAINT_BUCKET.get());
                        // Paint buckets (all colors)
                        out.accept(EgToolsItems.WHITE_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.LIGHT_GRAY_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.GRAY_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.BLACK_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.BROWN_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.RED_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.ORANGE_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.YELLOW_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.LIME_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.GREEN_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.CYAN_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.LIGHT_BLUE_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.BLUE_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.PURPLE_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.MAGENTA_PAINT_BUCKET.get());
                        out.accept(EgToolsItems.PINK_PAINT_BUCKET.get());
                    })
                    .build());
}

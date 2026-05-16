package com.styenvy.egtools;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EgToolsDataComponents {
    private EgToolsDataComponents() {}

    public static final DeferredRegister.DataComponents REGISTER =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, EgTools.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PaintBrushContents>> PAINT_BRUSH_CONTENTS =
            REGISTER.registerComponentType("paint_brush_contents", builder -> builder
                    .persistent(PaintBrushContents.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PAINT_BUCKET_REMAINING =
            REGISTER.registerComponentType("paint_bucket_remaining", builder -> builder
                    .persistent(ExtraCodecs.intRange(0, PaintBucketItem.MAX_PAINTS)));
}

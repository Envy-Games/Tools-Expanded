package com.styenvy.egtools;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;

public record PaintBrushContents(DyeColor color, int uses) {
    public static final int MAX_USES = 128;

    public static final Codec<PaintBrushContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DyeColor.CODEC.fieldOf("color").forGetter(PaintBrushContents::color),
            ExtraCodecs.intRange(1, MAX_USES).fieldOf("uses").forGetter(PaintBrushContents::uses)
    ).apply(instance, PaintBrushContents::new));

    public static PaintBrushContents of(DyeColor color, int uses) {
        return new PaintBrushContents(color, Mth.clamp(uses, 1, MAX_USES));
    }
}

package com.styenvy.egtools;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record MiningArea(int radius) {
    public static final Codec<MiningArea> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.intRange(1, 2).fieldOf("radius").forGetter(MiningArea::radius)
    ).apply(instance, MiningArea::new));
}

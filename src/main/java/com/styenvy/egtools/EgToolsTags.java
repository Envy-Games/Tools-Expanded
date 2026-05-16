package com.styenvy.egtools;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class EgToolsTags {
    private EgToolsTags() {}

    public static final class Blocks {
        public static final TagKey<Block> WEAKENABLE_BEDROCK = blockTag("weakenable_bedrock");

        private Blocks() {}
    }

    private static TagKey<Block> blockTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(EgTools.MODID, path));
    }
}

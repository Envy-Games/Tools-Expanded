package com.styenvy.egtools;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EgToolsEnchantmentEffectComponents {
    private EgToolsEnchantmentEffectComponents() {}

    public static final DeferredRegister.DataComponents REGISTER =
            DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, EgTools.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MiningArea>> MINING_AREA =
            REGISTER.registerComponentType("mining_area", builder -> builder.persistent(MiningArea.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AUTO_SMELT =
            REGISTER.registerComponentType("auto_smelt", builder -> builder.persistent(Codec.BOOL));
}

package com.styenvy.egtools;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EgToolsEnchantmentEffectComponents {
    private EgToolsEnchantmentEffectComponents() {}

    public static final DeferredRegister<DataComponentType<?>> REGISTER =
            DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, EgTools.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MiningArea>> MINING_AREA =
            REGISTER.register("mining_area", () -> DataComponentType.<MiningArea>builder()
                    .persistent(MiningArea.CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AUTO_SMELT =
            REGISTER.register("auto_smelt", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .build());
}

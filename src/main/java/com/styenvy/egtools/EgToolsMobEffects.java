package com.styenvy.egtools;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EgToolsMobEffects {
    private EgToolsMobEffects() {}

    public static final DeferredRegister<MobEffect> REGISTER =
            DeferredRegister.create(Registries.MOB_EFFECT, EgTools.MODID);

    public static final DeferredHolder<MobEffect, BedrockWeakeningMobEffect> BEDROCK_WEAKENING =
            REGISTER.register("bedrock_weakening", BedrockWeakeningMobEffect::new);
}

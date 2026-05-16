package com.styenvy.egtools;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EgToolsPotions {
    private EgToolsPotions() {}

    public static final int WEAKENING_DURATION_TICKS = 45 * 20;

    public static final DeferredRegister<Potion> REGISTER =
            DeferredRegister.create(Registries.POTION, EgTools.MODID);

    public static final DeferredHolder<Potion, Potion> WEAKENING = REGISTER.register("weakening",
            () -> new Potion(new MobEffectInstance(EgToolsMobEffects.BEDROCK_WEAKENING, WEAKENING_DURATION_TICKS)));
}

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
}

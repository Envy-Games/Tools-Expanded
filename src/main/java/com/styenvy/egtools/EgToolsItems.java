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

    // Diamond Paxel: tiered multi-tool with diamond stats
    public static final DeferredHolder<Item, PaxelItem> DIAMOND_PAXEL = REGISTER.register("diamond_paxel",
            () -> new PaxelItem(Tiers.DIAMOND, new Item.Properties().stacksTo(1)));

    // Netherite Paxel: tiered multi-tool with netherite stats (fire-resistant like vanilla)
    public static final DeferredHolder<Item, PaxelItem> NETHERITE_PAXEL = REGISTER.register("netherite_paxel",
            () -> new PaxelItem(Tiers.NETHERITE, new Item.Properties().stacksTo(1).fireResistant()));
}


package com.styenvy.egtools;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = EgTools.MODID) // defaults to MOD bus
public final class EgToolsTabs {
    private EgToolsTabs() {}

    public static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EgTools.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EGTOOLS_TAB = REGISTER.register("egtools",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.egtools"))
                    .icon(() -> EgToolsItems.DIAMOND_PAXEL.get().getDefaultInstance())
                    .displayItems((params, out) -> {
                        out.accept(EgToolsItems.IRON_PAXEL.get());
                        out.accept(EgToolsItems.DIAMOND_PAXEL.get());
                        out.accept(EgToolsItems.NETHERITE_PAXEL.get());
                    })
                    .build());

    @SubscribeEvent
    public static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            e.accept(EgToolsItems.IRON_PAXEL.get());
            e.accept(EgToolsItems.DIAMOND_PAXEL.get());
            e.accept(EgToolsItems.NETHERITE_PAXEL.get());
        }
    }
}
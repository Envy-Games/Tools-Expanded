package com.styenvy.egtools;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = EgTools.MODID, value = Dist.CLIENT)
public final class CoordHudClientModEvents {
    private CoordHudClientModEvents() {}

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        CoordHudClient.registerGuiLayers(event);
    }
}

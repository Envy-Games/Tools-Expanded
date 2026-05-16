package com.styenvy.egtools;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = EgTools.MODID, value = Dist.CLIENT)
public final class CoordHudClientGameEvents {
    private CoordHudClientGameEvents() {}

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        CoordHudClient.registerClientCommands(event);
    }
}

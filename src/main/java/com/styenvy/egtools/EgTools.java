package com.styenvy.egtools;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(EgTools.MODID)
public final class EgTools {
    public static final String MODID = "egtools";

    public EgTools(IEventBus modBus) {
        EgToolsItems.REGISTER.register(modBus);
        EgToolsTabs.REGISTER.register(modBus);
    }
}

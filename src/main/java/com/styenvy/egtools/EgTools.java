package com.styenvy.egtools;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(EgTools.MODID)
public class EgTools {
    public static final String MODID = "egtools";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EgTools(IEventBus modEventBus, ModContainer modContainer) {
        // Register all deferred registers
        EgToolsItems.REGISTER.register(modEventBus);
        EgToolsTabs.REGISTER.register(modEventBus);
        EgToolsRecipeSerializers.REGISTER.register(modEventBus);

        LOGGER.info("EgTools mod initialized!");
    }
}
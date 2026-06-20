package com.oblixorprime.immersiveego.civitas;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(ImmersiveEgoCivitas.MOD_ID)
public final class ImmersiveEgoCivitas {
    public static final String MOD_ID = "immersive_ego_civitas";
    public static final String DISPLAY_NAME = "Immersive EGO: CIVITAS";

    private static final Logger LOGGER = LogUtils.getLogger();

    public ImmersiveEgoCivitas(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        LOGGER.info("{} bootstrap registered.", DISPLAY_NAME);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        CivitasRuntimeDependencyGuard.verifyPinnedRuntime(LOGGER);
        LOGGER.info("{} common setup complete; integration surfaces are intentionally neutral.", MOD_ID);
    }
}

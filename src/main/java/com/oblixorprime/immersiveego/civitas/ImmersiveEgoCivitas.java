package com.oblixorprime.immersiveego.civitas;

import com.mojang.logging.LogUtils;
import com.oblixorprime.immersiveego.civitas.command.CivitasServerCommands;
import com.oblixorprime.immersiveego.civitas.resident.upstream.UpstreamResidentHostAdapters;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
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
        NeoForge.EVENT_BUS.addListener(CivitasServerCommands::register);
        LOGGER.info("{} bootstrap registered.", DISPLAY_NAME);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        CivitasRuntimeDependencyGuard.verifyPinnedRuntime(LOGGER);
        var residentHostAdapters = UpstreamResidentHostAdapters.createRegistry();
        LOGGER.info(
                "{} resident host adapters registered: {}",
                MOD_ID,
                residentHostAdapters.adapters().stream()
                        .map(adapter -> adapter.authority().serializedId())
                        .toList());
        LOGGER.info("{} common setup complete; integration surfaces are intentionally neutral.", MOD_ID);
    }
}

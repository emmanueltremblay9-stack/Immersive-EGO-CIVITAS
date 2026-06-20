package com.oblixorprime.immersiveego.civitas.gametest;

import com.oblixorprime.immersiveego.civitas.ImmersiveEgoCivitas;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ImmersiveEgoCivitas.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CivitasGameTests {
    private static final String EMPTY = "empty";

    private CivitasGameTests() {
    }

    @GameTest(template = EMPTY, timeoutTicks = 20)
    public static void serverRuntimeBoots(GameTestHelper helper) {
        helper.assertTrue(
                ImmersiveEgoCivitas.MOD_ID.equals("immersive_ego_civitas"),
                "mod id must match the registered GameTest namespace"
        );
        helper.succeed();
    }
}

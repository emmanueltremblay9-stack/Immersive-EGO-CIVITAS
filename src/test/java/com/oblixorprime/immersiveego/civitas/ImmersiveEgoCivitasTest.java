package com.oblixorprime.immersiveego.civitas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ImmersiveEgoCivitasTest {
    @Test
    void modIdentityMatchesMetadataPin() {
        assertEquals("immersive_ego_civitas", ImmersiveEgoCivitas.MOD_ID);
        assertEquals("Immersive EGO: CIVITAS", ImmersiveEgoCivitas.DISPLAY_NAME);
    }
}

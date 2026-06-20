package com.oblixorprime.immersiveego.civitas.resident;

import java.util.Arrays;
import java.util.Optional;

public enum CivitasAuthority {
    CIVITAS("civitas"),
    IMMERSIVE_EGO("immersive_ego"),
    MINECOLONIES("minecolonies"),
    MCA_REBORN("mca"),
    MODERN_COMPANIONS("modern_companions");

    private final String serializedId;

    CivitasAuthority(String serializedId) {
        this.serializedId = serializedId;
    }

    public String serializedId() {
        return serializedId;
    }

    public static Optional<CivitasAuthority> bySerializedId(String serializedId) {
        return Arrays.stream(values())
                .filter(authority -> authority.serializedId.equals(serializedId))
                .findFirst();
    }
}

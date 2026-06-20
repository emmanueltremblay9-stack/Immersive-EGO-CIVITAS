package com.oblixorprime.immersiveego.civitas.resident.upstream;

import java.util.Objects;

public record MineColoniesCitizenTarget(boolean resolved, Object citizenData, String message) {
    public MineColoniesCitizenTarget {
        message = Objects.requireNonNullElse(message, "");
        if (resolved) {
            Objects.requireNonNull(citizenData, "citizenData");
        }
    }

    public static MineColoniesCitizenTarget resolved(Object citizenData, String message) {
        return new MineColoniesCitizenTarget(true, citizenData, message);
    }

    public static MineColoniesCitizenTarget failed(String message) {
        return new MineColoniesCitizenTarget(false, null, message);
    }
}

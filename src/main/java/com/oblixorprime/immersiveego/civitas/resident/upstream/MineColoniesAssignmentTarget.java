package com.oblixorprime.immersiveego.civitas.resident.upstream;

import java.util.Objects;

public record MineColoniesAssignmentTarget(
        boolean resolved,
        Object citizenData,
        Object targetHomeBuilding,
        Object targetWorkBuilding,
        String message) {
    public MineColoniesAssignmentTarget {
        message = Objects.requireNonNullElse(message, "");
        if (resolved) {
            Objects.requireNonNull(citizenData, "citizenData");
            Objects.requireNonNull(targetHomeBuilding, "targetHomeBuilding");
        }
    }

    public static MineColoniesAssignmentTarget homeOnly(
            Object citizenData,
            Object targetHomeBuilding,
            String message) {
        return new MineColoniesAssignmentTarget(
                true,
                citizenData,
                targetHomeBuilding,
                null,
                message);
    }

    public static MineColoniesAssignmentTarget homeAndWork(
            Object citizenData,
            Object targetHomeBuilding,
            Object targetWorkBuilding,
            String message) {
        return new MineColoniesAssignmentTarget(
                true,
                citizenData,
                targetHomeBuilding,
                Objects.requireNonNull(targetWorkBuilding, "targetWorkBuilding"),
                message);
    }

    public static MineColoniesAssignmentTarget failed(String message) {
        return new MineColoniesAssignmentTarget(false, null, null, null, message);
    }
}

package com.oblixorprime.immersiveego.civitas.resident.upstream;

public record MineColoniesAssignmentPlan(
        Object citizenData,
        Object targetHomeModule,
        Object previousHomeModule,
        Object targetWorkModule,
        Object previousWorkModule) {
    public MineColoniesAssignmentPlan {
        if (citizenData == null) {
            throw new IllegalArgumentException("citizenData is required");
        }
        if (targetHomeModule == null && targetWorkModule == null) {
            throw new IllegalArgumentException("at least one target assignment module is required");
        }
    }

    public static MineColoniesAssignmentPlan homeOnly(
            Object citizenData,
            Object targetHomeModule,
            Object previousHomeModule) {
        return new MineColoniesAssignmentPlan(citizenData, targetHomeModule, previousHomeModule, null, null);
    }

    public static MineColoniesAssignmentPlan homeAndWork(
            Object citizenData,
            Object targetHomeModule,
            Object previousHomeModule,
            Object targetWorkModule,
            Object previousWorkModule) {
        return new MineColoniesAssignmentPlan(
                citizenData,
                targetHomeModule,
                previousHomeModule,
                targetWorkModule,
                previousWorkModule);
    }
}

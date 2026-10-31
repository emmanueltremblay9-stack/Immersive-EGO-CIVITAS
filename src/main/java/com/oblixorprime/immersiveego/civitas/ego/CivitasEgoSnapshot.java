package com.oblixorprime.immersiveego.civitas.ego;

import java.util.Optional;
import java.util.OptionalDouble;

public record CivitasEgoSnapshot(
        double readiness,
        double hydration,
        double nutritionBalance,
        double stamina,
        double fatigue,
        double stress,
        double thermalStrain,
        double respiratoryStrain,
        double recoveryCapacity,
        OptionalDouble sleepPressure,
        Optional<String> originId) {
    public CivitasEgoSnapshot {
        readiness = clamp01(readiness);
        hydration = clamp01(hydration);
        nutritionBalance = clamp01(nutritionBalance);
        stamina = clamp01(stamina);
        fatigue = clamp01(fatigue);
        stress = clamp01(stress);
        thermalStrain = clamp01(thermalStrain);
        respiratoryStrain = clamp01(respiratoryStrain);
        recoveryCapacity = clamp01(recoveryCapacity);
        sleepPressure = sleepPressure == null
                ? OptionalDouble.empty()
                : sleepPressure.stream()
                        .map(CivitasEgoSnapshot::clamp01)
                        .findFirst();
        originId = originId == null ? Optional.empty() : originId.filter(value -> !value.isBlank());
    }

    public static CivitasEgoSnapshot neutral() {
        return new CivitasEgoSnapshot(
                1.0D,
                1.0D,
                1.0D,
                1.0D,
                0.0D,
                0.0D,
                0.0D,
                0.0D,
                1.0D,
                OptionalDouble.empty(),
                Optional.empty());
    }

    public boolean canWork() {
        return readiness >= 0.35D
                && hydration >= 0.25D
                && nutritionBalance >= 0.25D
                && fatigue <= 0.80D
                && stress <= 0.85D;
    }

    public boolean canFight() {
        return readiness >= 0.55D
                && hydration >= 0.45D
                && nutritionBalance >= 0.45D
                && stamina >= 0.45D
                && fatigue <= 0.65D
                && stress <= 0.65D
                && thermalStrain <= 0.75D
                && respiratoryStrain <= 0.75D;
    }

    public boolean canTravel() {
        return readiness >= 0.40D
                && hydration >= 0.25D
                && nutritionBalance >= 0.25D
                && stamina >= 0.35D
                && fatigue <= 0.85D;
    }

    public boolean canRecover() {
        return recoveryCapacity >= 0.25D
                && hydration >= 0.15D
                && nutritionBalance >= 0.15D;
    }

    private static double clamp01(double value) {
        if (Double.isNaN(value)) {
            return 0.0D;
        }
        return Math.max(0.0D, Math.min(1.0D, value));
    }
}

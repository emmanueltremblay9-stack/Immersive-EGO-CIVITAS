package com.oblixorprime.immersiveego.civitas.ego;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.OptionalDouble;

public final class CivitasEgoStateReader {
    private CivitasEgoStateReader() {
    }

    public static Optional<CivitasEgoSnapshot> readSnapshot(Object upstreamEgoState) {
        if (upstreamEgoState == null) {
            return Optional.empty();
        }

        OptionalDouble readiness = invokeDouble(upstreamEgoState, "readiness");
        OptionalDouble hydration = invokeDouble(upstreamEgoState, "hydration");
        OptionalDouble nutritionBalance = invokeDouble(upstreamEgoState, "nutritionBalance");
        OptionalDouble stamina = invokeDouble(upstreamEgoState, "stamina");
        OptionalDouble fatigue = invokeDouble(upstreamEgoState, "fatigue");
        OptionalDouble stress = invokeDouble(upstreamEgoState, "stress");
        OptionalDouble thermalStrain = invokeDouble(upstreamEgoState, "thermalStrain");
        OptionalDouble respiratoryStrain = invokeDouble(upstreamEgoState, "respiratoryStrain");
        OptionalDouble recoveryCapacity = invokeDouble(upstreamEgoState, "recoveryCapacity");

        if (readiness.isEmpty()
                || hydration.isEmpty()
                || nutritionBalance.isEmpty()
                || stamina.isEmpty()
                || fatigue.isEmpty()
                || stress.isEmpty()
                || thermalStrain.isEmpty()
                || respiratoryStrain.isEmpty()
                || recoveryCapacity.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new CivitasEgoSnapshot(
                readiness.getAsDouble(),
                hydration.getAsDouble(),
                nutritionBalance.getAsDouble(),
                stamina.getAsDouble(),
                fatigue.getAsDouble(),
                stress.getAsDouble(),
                thermalStrain.getAsDouble(),
                respiratoryStrain.getAsDouble(),
                recoveryCapacity.getAsDouble(),
                OptionalDouble.empty(),
                Optional.empty()));
    }

    private static OptionalDouble invokeDouble(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            Object value = method.invoke(target);
            if (value instanceof Number number) {
                return OptionalDouble.of(number.doubleValue());
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | RuntimeException ignored) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.empty();
    }
}

package com.oblixorprime.immersiveego.civitas.ego;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class CivitasEgoApiContract {
    private static final List<RequiredClass> REQUIRED_STATE_CLASSES = List.of(
            required(
                    "com.oblixorprime.immersiveego.data.EgoState",
                    List.of(
                            "defaultState",
                            "canReadSchemaVersion",
                            "readiness",
                            "hydration",
                            "nutritionBalance",
                            "stamina",
                            "fatigue",
                            "stress",
                            "thermalStrain",
                            "respiratoryStrain",
                            "recoveryCapacity"),
                    List.of()),
            required(
                    "com.oblixorprime.immersiveego.registry.EgoAttachments",
                    List.of(),
                    List.of("EGO_STATE")),
            required(
                    "com.oblixorprime.immersiveego.simulation.EgoSimulationFrameCalculator",
                    List.of("advanceFrame"),
                    List.of()),
            required(
                    "com.oblixorprime.immersiveego.config.EgoDynamicServerConfig",
                    List.of("defaults"),
                    List.of()),
            required(
                    "com.oblixorprime.immersiveego.config.EgoDynamicServerConfigLoader",
                    List.of("current"),
                    List.of()));

    private CivitasEgoApiContract() {
    }

    public static CivitasEgoApiStatus inspect(ClassLoader classLoader) {
        List<String> missingMembers = missingMembers(classLoader);
        List<String> missingRequirements = new ArrayList<>();
        if (!missingMembers.isEmpty()) {
            missingRequirements.add("Immersive EGO state snapshot surface is incomplete.");
        }
        missingRequirements.add(
                "Immersive EGO alpha.44 exposes player EGO_STATE data but no documented NPC subject "
                        + "registration or NPC snapshot API.");
        return new CivitasEgoApiStatus(
                missingMembers.isEmpty(),
                false,
                missingMembers,
                missingRequirements);
    }

    public static List<RequiredClass> requiredStateClasses() {
        return REQUIRED_STATE_CLASSES;
    }

    public static List<String> missingMembers(ClassLoader classLoader) {
        List<String> missing = new ArrayList<>();
        for (RequiredClass requiredClass : REQUIRED_STATE_CLASSES) {
            Class<?> type = loadClass(requiredClass.className(), classLoader, missing);
            if (type == null) {
                continue;
            }
            for (String methodName : requiredClass.publicMethodNames()) {
                if (!hasPublicMethod(type, methodName)) {
                    missing.add(requiredClass.className() + "#" + methodName + "(...)");
                }
            }
            for (String fieldName : requiredClass.publicFieldNames()) {
                if (!hasPublicField(type, fieldName)) {
                    missing.add(requiredClass.className() + "#" + fieldName);
                }
            }
        }
        return List.copyOf(missing);
    }

    private static Class<?> loadClass(String className, ClassLoader classLoader, List<String> missing) {
        try {
            return Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException exception) {
            missing.add(className);
            return null;
        }
    }

    private static boolean hasPublicMethod(Class<?> type, String methodName) {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(methodName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasPublicField(Class<?> type, String fieldName) {
        try {
            Field field = type.getField(fieldName);
            return field != null;
        } catch (NoSuchFieldException exception) {
            return false;
        }
    }

    private static RequiredClass required(
            String className,
            List<String> publicMethodNames,
            List<String> publicFieldNames) {
        return new RequiredClass(className, publicMethodNames, publicFieldNames);
    }

    public record RequiredClass(
            String className,
            List<String> publicMethodNames,
            List<String> publicFieldNames) {
        public RequiredClass {
            publicMethodNames = List.copyOf(publicMethodNames);
            publicFieldNames = List.copyOf(publicFieldNames);
        }
    }
}

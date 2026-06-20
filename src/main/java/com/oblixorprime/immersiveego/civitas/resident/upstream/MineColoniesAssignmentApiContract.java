package com.oblixorprime.immersiveego.civitas.resident.upstream;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class MineColoniesAssignmentApiContract {
    private static final String MINECOLONIES_API = "com.minecolonies.api.IMinecoloniesAPI";
    private static final String COLONY_MANAGER = "com.minecolonies.api.colony.IColonyManager";
    private static final String COLONY = "com.minecolonies.api.colony.IColony";
    private static final String LEVEL = "net.minecraft.world.level.Level";
    private static final String BLOCK_POS = "net.minecraft.core.BlockPos";
    private static final String CITIZEN_DATA = "com.minecolonies.api.colony.ICitizenData";
    private static final String CITIZEN_MANAGER =
            "com.minecolonies.api.colony.managers.interfaces.ICitizenManager";
    private static final String BUILDING = "com.minecolonies.api.colony.buildings.IBuilding";
    private static final String BUILDING_MODULE =
            "com.minecolonies.api.colony.buildings.modules.IBuildingModule";
    private static final String ASSIGNS_CITIZEN =
            "com.minecolonies.api.colony.buildings.modules.IAssignsCitizen";
    private static final String ASSIGNS_JOB =
            "com.minecolonies.api.colony.buildings.modules.IAssignsJob";
    private static final String JOB = "com.minecolonies.api.colony.jobs.IJob";
    private static final String JOB_ENTRY = "com.minecolonies.api.colony.jobs.registry.JobEntry";

    private static final List<RequiredMethod> REQUIRED_METHODS = List.of(
            required(MINECOLONIES_API, "getInstance", MINECOLONIES_API),
            required(MINECOLONIES_API, "getColonyManager", COLONY_MANAGER),
            required(COLONY_MANAGER, "getColonyByWorld", COLONY, "int", LEVEL),
            required(COLONY_MANAGER, "getBuilding", BUILDING, LEVEL, BLOCK_POS),
            required(COLONY, "getID", "int"),
            required(COLONY, "getCitizenManager", CITIZEN_MANAGER),
            required(CITIZEN_MANAGER, "getCivilian", CITIZEN_DATA, "int"),
            required(CITIZEN_DATA, "getHomeBuilding", BUILDING),
            required(CITIZEN_DATA, "setHomeBuilding", "void", BUILDING),
            required(CITIZEN_DATA, "getWorkBuilding", BUILDING),
            required(CITIZEN_DATA, "getJob", JOB),
            required(CITIZEN_DATA, "setJob", "void", JOB),
            required(BUILDING_MODULE, "getBuilding", BUILDING),
            required(BUILDING, "getColony", COLONY),
            required(BUILDING, "getModulesByType", "java.util.List", "java.lang.Class"),
            required(BUILDING, "getAllAssignedCitizen", "java.util.Set"),
            required(BUILDING, "cancelAllRequestsOfCitizenOrBuilding", "void", CITIZEN_DATA),
            required(BUILDING, "markDirty", "void"),
            required(ASSIGNS_CITIZEN, "assignCitizen", "boolean", CITIZEN_DATA),
            required(ASSIGNS_CITIZEN, "removeCitizen", "boolean", CITIZEN_DATA),
            required(ASSIGNS_CITIZEN, "getAssignedCitizen", "java.util.List"),
            required(ASSIGNS_CITIZEN, "isFull", "boolean"),
            required(ASSIGNS_CITIZEN, "hasAssignedCitizen", "boolean", CITIZEN_DATA),
            required(ASSIGNS_CITIZEN, "hasAssignedCitizen", "boolean"),
            required(ASSIGNS_JOB, "getJobEntry", JOB_ENTRY),
            required(JOB, "assignTo", "boolean", ASSIGNS_JOB),
            required(JOB, "getWorkBuilding", BUILDING),
            required(JOB, "getWorkModule", ASSIGNS_JOB),
            required(JOB, "onRemoval", "void"));

    private MineColoniesAssignmentApiContract() {
    }

    public static List<RequiredMethod> requiredMethods() {
        return REQUIRED_METHODS;
    }

    public static List<String> missingMembers(ClassLoader classLoader) {
        List<String> missing = new ArrayList<>();
        for (RequiredMethod requiredMethod : REQUIRED_METHODS) {
            Class<?> owner = loadType(requiredMethod.ownerClassName(), classLoader, missing);
            Class<?> returnType = loadType(requiredMethod.returnTypeName(), classLoader, missing);
            Class<?>[] parameterTypes = loadParameterTypes(requiredMethod.parameterTypeNames(), classLoader, missing);
            if (owner == null || returnType == null || parameterTypes == null) {
                continue;
            }

            Method method;
            try {
                method = owner.getMethod(requiredMethod.methodName(), parameterTypes);
            } catch (NoSuchMethodException exception) {
                missing.add(requiredMethod.descriptor());
                continue;
            }

            if (!method.getReturnType().equals(returnType)) {
                missing.add(requiredMethod.descriptor() + " returns " + method.getReturnType().getName());
            }
        }
        return List.copyOf(missing);
    }

    private static Class<?>[] loadParameterTypes(
            List<String> parameterTypeNames,
            ClassLoader classLoader,
            List<String> missing) {
        Class<?>[] parameterTypes = new Class<?>[parameterTypeNames.size()];
        for (int index = 0; index < parameterTypeNames.size(); index++) {
            Class<?> parameterType = loadType(parameterTypeNames.get(index), classLoader, missing);
            if (parameterType == null) {
                return null;
            }
            parameterTypes[index] = parameterType;
        }
        return parameterTypes;
    }

    private static Class<?> loadType(String typeName, ClassLoader classLoader, List<String> missing) {
        return switch (typeName) {
            case "boolean" -> boolean.class;
            case "int" -> int.class;
            case "void" -> void.class;
            default -> loadClass(typeName, classLoader, missing);
        };
    }

    private static Class<?> loadClass(String className, ClassLoader classLoader, List<String> missing) {
        try {
            return Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException exception) {
            missing.add(className);
            return null;
        }
    }

    private static RequiredMethod required(
            String ownerClassName,
            String methodName,
            String returnTypeName,
            String... parameterTypeNames) {
        return new RequiredMethod(ownerClassName, methodName, returnTypeName, List.of(parameterTypeNames));
    }

    public record RequiredMethod(
            String ownerClassName,
            String methodName,
            String returnTypeName,
            List<String> parameterTypeNames) {
        public RequiredMethod {
            parameterTypeNames = List.copyOf(parameterTypeNames);
        }

        String descriptor() {
            return ownerClassName + "#" + methodName + "(" + String.join(", ", parameterTypeNames) + ")";
        }
    }
}

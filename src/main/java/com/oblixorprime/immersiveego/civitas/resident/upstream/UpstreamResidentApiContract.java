package com.oblixorprime.immersiveego.civitas.resident.upstream;

import java.util.ArrayList;
import java.util.List;

public final class UpstreamResidentApiContract {
    private static final List<RequiredClass> REQUIRED_CLASSES = List.of(
            required(
                    McaVillagerResidentHostAdapter.VILLAGER_ENTITY_CLASS,
                    "getUUID",
                    "getRelationships",
                    "getResidency",
                    "getGenetics",
                    "getAgeState"),
            required(
                    "net.conczin.mca.server.world.data.FamilyTreeNode",
                    "id",
                    "father",
                    "mother",
                    "partner",
                    "children",
                    "gender"),
            required(
                    MineColoniesCitizenResidentHostAdapter.CITIZEN_INTERFACE,
                    "getId",
                    "getColony",
                    "getName",
                    "isChild"),
            required(
                    MineColoniesCitizenResidentHostAdapter.CITIZEN_DATA_INTERFACE,
                    "getEntity",
                    "getHomeBuilding",
                    "getWorkBuilding",
                    "getPartner",
                    "getChildren",
                    "getSiblings",
                    "getParents"),
            required(
                    "com.minecolonies.api.colony.IColony",
                    "getID",
                    "getCitizenManager"),
            required(
                    "com.minecolonies.api.colony.managers.interfaces.ICitizenManager",
                    "getCitizens",
                    "getJoblessCitizen"),
            required(
                    MineColoniesCitizenResidentHostAdapter.ABSTRACT_CITIZEN_CLASS,
                    "getCitizenData",
                    "getCitizenColonyHandler",
                    "getCivilianID"),
            required(
                    "com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenColonyHandler",
                    "getColonyId",
                    "getColony"));

    private UpstreamResidentApiContract() {
    }

    public static List<RequiredClass> requiredClasses() {
        return REQUIRED_CLASSES;
    }

    public static List<String> missingMembers(ClassLoader classLoader) {
        List<String> missing = new ArrayList<>();
        for (RequiredClass requiredClass : REQUIRED_CLASSES) {
            Class<?> type = loadClass(requiredClass.className(), classLoader, missing);
            if (type == null) {
                continue;
            }
            for (String methodName : requiredClass.noArgMethodNames()) {
                try {
                    type.getMethod(methodName);
                } catch (NoSuchMethodException exception) {
                    missing.add(requiredClass.className() + "#" + methodName + "()");
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

    private static RequiredClass required(String className, String... noArgMethodNames) {
        return new RequiredClass(className, List.of(noArgMethodNames));
    }

    public record RequiredClass(String className, List<String> noArgMethodNames) {
        public RequiredClass {
            noArgMethodNames = List.copyOf(noArgMethodNames);
        }
    }
}

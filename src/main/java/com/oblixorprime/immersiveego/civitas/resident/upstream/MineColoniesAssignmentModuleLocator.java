package com.oblixorprime.immersiveego.civitas.resident.upstream;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MineColoniesAssignmentModuleLocator {
    private static final String ASSIGNS_CITIZEN =
            "com.minecolonies.api.colony.buildings.modules.IAssignsCitizen";
    private static final String ASSIGNS_JOB =
            "com.minecolonies.api.colony.buildings.modules.IAssignsJob";

    private final Class<?> assignsCitizenType;
    private final Class<?> assignsJobType;

    public MineColoniesAssignmentModuleLocator() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public MineColoniesAssignmentModuleLocator(ClassLoader classLoader) {
        this(
                loadRequiredType(runtimeClassLoader(classLoader), ASSIGNS_CITIZEN),
                loadRequiredType(runtimeClassLoader(classLoader), ASSIGNS_JOB));
    }

    MineColoniesAssignmentModuleLocator(Class<?> assignsCitizenType, Class<?> assignsJobType) {
        this.assignsCitizenType = assignsCitizenType;
        this.assignsJobType = assignsJobType;
    }

    public static List<String> missingRuntimeTypes(ClassLoader classLoader) {
        ClassLoader runtimeClassLoader = runtimeClassLoader(classLoader);
        List<String> missing = new ArrayList<>();
        if (findType(runtimeClassLoader, ASSIGNS_CITIZEN).isEmpty()) {
            missing.add(ASSIGNS_CITIZEN);
        }
        if (findType(runtimeClassLoader, ASSIGNS_JOB).isEmpty()) {
            missing.add(ASSIGNS_JOB);
        }
        return List.copyOf(missing);
    }

    public MineColoniesAssignmentResolution homeOnlyPlan(Object citizenData, Object targetHomeBuilding) {
        if (citizenData == null) {
            return MineColoniesAssignmentResolution.failed("citizenData is required");
        }
        if (targetHomeBuilding == null) {
            return MineColoniesAssignmentResolution.failed("target home building is required");
        }

        ModuleSelection targetHome = selectTargetHomeModule(targetHomeBuilding, citizenData);
        if (!targetHome.succeeded()) {
            return MineColoniesAssignmentResolution.failed(targetHome.message());
        }

        ModuleSelection previousHome = selectPreviousHomeModule(citizenData);
        if (!previousHome.succeeded()) {
            return MineColoniesAssignmentResolution.failed(previousHome.message());
        }

        return MineColoniesAssignmentResolution.resolved(
                MineColoniesAssignmentPlan.homeOnly(
                        citizenData,
                        targetHome.module(),
                        previousHome.module()),
                "MineColonies home assignment modules resolved");
    }

    public MineColoniesAssignmentResolution homeAndWorkPlan(
            Object citizenData,
            Object targetHomeBuilding,
            Object targetWorkBuilding) {
        if (citizenData == null) {
            return MineColoniesAssignmentResolution.failed("citizenData is required");
        }
        if (targetHomeBuilding == null && targetWorkBuilding == null) {
            return MineColoniesAssignmentResolution.failed(
                    "at least one target building is required");
        }
        if (targetWorkBuilding == null) {
            return homeOnlyPlan(citizenData, targetHomeBuilding);
        }

        ModuleSelection targetHome = ModuleSelection.skipped();
        ModuleSelection previousHome = ModuleSelection.skipped();
        if (targetHomeBuilding != null) {
            targetHome = selectTargetHomeModule(targetHomeBuilding, citizenData);
            if (!targetHome.succeeded()) {
                return MineColoniesAssignmentResolution.failed(targetHome.message());
            }

            previousHome = selectPreviousHomeModule(citizenData);
            if (!previousHome.succeeded()) {
                return MineColoniesAssignmentResolution.failed(previousHome.message());
            }
        }

        ModuleSelection targetWork = selectTargetWorkModule(targetWorkBuilding, citizenData);
        if (!targetWork.succeeded()) {
            return MineColoniesAssignmentResolution.failed(targetWork.message());
        }

        ModuleSelection previousWork = selectPreviousWorkModule(citizenData);
        if (!previousWork.succeeded()) {
            return MineColoniesAssignmentResolution.failed(previousWork.message());
        }

        return MineColoniesAssignmentResolution.resolved(
                MineColoniesAssignmentPlan.homeAndWork(
                        citizenData,
                        targetHome.module(),
                        previousHome.module(),
                        targetWork.module(),
                        previousWork.module()),
                "MineColonies home/work assignment modules resolved");
    }

    private ModuleSelection selectTargetHomeModule(Object building, Object citizenData) {
        return selectTargetModule(
                "home",
                modulesByType(building, assignsCitizenType).stream()
                        .filter(module -> !assignsJobType.isInstance(module))
                        .toList(),
                citizenData);
    }

    private ModuleSelection selectTargetWorkModule(Object building, Object citizenData) {
        return selectTargetModule(
                "work",
                modulesByType(building, assignsJobType),
                citizenData);
    }

    private ModuleSelection selectTargetModule(String label, List<Object> modules, Object citizenData) {
        if (modules.isEmpty()) {
            return ModuleSelection.failed("no MineColonies " + label + " assignment module found");
        }

        List<Object> alreadyAssigned = modules.stream()
                .filter(module -> hasAssignedCitizen(module, citizenData))
                .toList();
        if (alreadyAssigned.size() == 1) {
            return ModuleSelection.selected(alreadyAssigned.getFirst());
        }
        if (alreadyAssigned.size() > 1) {
            return ModuleSelection.failed("multiple MineColonies " + label
                    + " modules already contain the citizen");
        }

        List<Object> openModules = modules.stream()
                .filter(module -> !isFull(module))
                .toList();
        if (openModules.size() == 1) {
            return ModuleSelection.selected(openModules.getFirst());
        }
        if (openModules.isEmpty()) {
            return ModuleSelection.failed("all MineColonies " + label
                    + " assignment modules are full");
        }
        return ModuleSelection.failed("multiple open MineColonies " + label
                + " assignment modules found");
    }

    private ModuleSelection selectPreviousHomeModule(Object citizenData) {
        Optional<Object> homeBuilding = ReflectiveHostAccess.invokeNoArg(citizenData, "getHomeBuilding");
        if (homeBuilding.isEmpty()) {
            return ModuleSelection.skipped();
        }

        List<Object> assignedHomeModules = modulesByType(homeBuilding.get(), assignsCitizenType).stream()
                .filter(module -> !assignsJobType.isInstance(module))
                .filter(module -> hasAssignedCitizen(module, citizenData))
                .toList();
        if (assignedHomeModules.size() > 1) {
            return ModuleSelection.failed("multiple previous MineColonies home modules contain the citizen");
        }
        return assignedHomeModules.isEmpty()
                ? ModuleSelection.skipped()
                : ModuleSelection.selected(assignedHomeModules.getFirst());
    }

    private ModuleSelection selectPreviousWorkModule(Object citizenData) {
        Optional<Object> currentJob = ReflectiveHostAccess.invokeNoArg(citizenData, "getJob");
        if (currentJob.isEmpty()) {
            return ModuleSelection.skipped();
        }

        Optional<Object> workModule = ReflectiveHostAccess.invokeNoArg(currentJob.get(), "getWorkModule");
        if (workModule.isEmpty()) {
            return ModuleSelection.skipped();
        }
        if (!assignsJobType.isInstance(workModule.get())) {
            return ModuleSelection.failed("previous MineColonies work module does not implement IAssignsJob");
        }
        return ModuleSelection.selected(workModule.get());
    }

    private List<Object> modulesByType(Object building, Class<?> moduleType) {
        Optional<Object> value = ReflectiveHostAccess.invoke(building, "getModulesByType", moduleType);
        if (value.isEmpty() || !(value.get() instanceof List<?> modules)) {
            return List.of();
        }

        return modules.stream()
                .filter(moduleType::isInstance)
                .map(module -> (Object) module)
                .toList();
    }

    private boolean hasAssignedCitizen(Object module, Object citizenData) {
        return ReflectiveHostAccess.invoke(module, "hasAssignedCitizen", citizenData)
                .filter(Boolean.class::isInstance)
                .map(Boolean.class::cast)
                .orElse(false);
    }

    private boolean isFull(Object module) {
        return ReflectiveHostAccess.invokeNoArg(module, "isFull")
                .filter(Boolean.class::isInstance)
                .map(Boolean.class::cast)
                .orElse(true);
    }

    private static Class<?> loadRequiredType(ClassLoader classLoader, String className) {
        return findType(classLoader, className).orElseThrow(
                () -> new IllegalStateException("Missing MineColonies runtime type: " + className));
    }

    private static Optional<Class<?>> findType(ClassLoader classLoader, String className) {
        try {
            return Optional.of(Class.forName(className, false, classLoader));
        } catch (ClassNotFoundException exception) {
            return Optional.empty();
        }
    }

    private static ClassLoader runtimeClassLoader(ClassLoader classLoader) {
        return classLoader == null
                ? MineColoniesAssignmentModuleLocator.class.getClassLoader()
                : classLoader;
    }

    private record ModuleSelection(boolean succeeded, Object module, String message) {
        static ModuleSelection selected(Object module) {
            return new ModuleSelection(true, module, "selected");
        }

        static ModuleSelection skipped() {
            return new ModuleSelection(true, null, "skipped");
        }

        static ModuleSelection failed(String message) {
            return new ModuleSelection(false, null, message);
        }
    }
}

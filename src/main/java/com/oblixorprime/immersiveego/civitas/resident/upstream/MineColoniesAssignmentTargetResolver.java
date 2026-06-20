package com.oblixorprime.immersiveego.civitas.resident.upstream;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;

public final class MineColoniesAssignmentTargetResolver {
    static final String MINECOLONIES_API = "com.minecolonies.api.IMinecoloniesAPI";

    private final Supplier<Optional<Object>> colonyManagerSupplier;

    public MineColoniesAssignmentTargetResolver() {
        this(MineColoniesAssignmentTargetResolver::runtimeColonyManager);
    }

    MineColoniesAssignmentTargetResolver(Supplier<Optional<Object>> colonyManagerSupplier) {
        this.colonyManagerSupplier = Objects.requireNonNull(colonyManagerSupplier, "colonyManagerSupplier");
    }

    public MineColoniesAssignmentTarget resolveHomeOnly(
            Object level,
            int colonyId,
            int citizenId,
            Object targetHomePosition) {
        Optional<Object> colonyManager = colonyManager();
        if (colonyManager.isEmpty()) {
            return MineColoniesAssignmentTarget.failed("MineColonies colony manager is unavailable");
        }

        Lookup lookup = resolveColonyAndCitizen(colonyManager.get(), level, colonyId, citizenId);
        if (!lookup.resolved()) {
            return MineColoniesAssignmentTarget.failed(lookup.message());
        }

        BuildingLookup home = buildingAt(
                colonyManager.get(),
                level,
                colonyId,
                targetHomePosition,
                "home");
        if (!home.resolved()) {
            return MineColoniesAssignmentTarget.failed(home.message());
        }

        return MineColoniesAssignmentTarget.homeOnly(
                lookup.citizenData(),
                home.building(),
                "resolved MineColonies citizen and home building");
    }

    public MineColoniesCitizenTarget resolveCitizen(Object level, int colonyId, int citizenId) {
        Optional<Object> colonyManager = colonyManager();
        if (colonyManager.isEmpty()) {
            return MineColoniesCitizenTarget.failed("MineColonies colony manager is unavailable");
        }

        Lookup lookup = resolveColonyAndCitizen(colonyManager.get(), level, colonyId, citizenId);
        if (!lookup.resolved()) {
            return MineColoniesCitizenTarget.failed(lookup.message());
        }

        return MineColoniesCitizenTarget.resolved(
                lookup.citizenData(),
                "resolved MineColonies citizen");
    }

    public MineColoniesAssignmentTarget resolveHomeAndWork(
            Object level,
            int colonyId,
            int citizenId,
            Object targetHomePosition,
            Object targetWorkPosition) {
        MineColoniesAssignmentTarget homeOnly =
                resolveHomeOnly(level, colonyId, citizenId, targetHomePosition);
        if (!homeOnly.resolved()) {
            return homeOnly;
        }

        Optional<Object> colonyManager = colonyManager();
        if (colonyManager.isEmpty()) {
            return MineColoniesAssignmentTarget.failed("MineColonies colony manager is unavailable");
        }

        BuildingLookup work = buildingAt(
                colonyManager.get(),
                level,
                colonyId,
                targetWorkPosition,
                "work");
        if (!work.resolved()) {
            return MineColoniesAssignmentTarget.failed(work.message());
        }

        return MineColoniesAssignmentTarget.homeAndWork(
                homeOnly.citizenData(),
                homeOnly.targetHomeBuilding(),
                work.building(),
                "resolved MineColonies citizen, home building, and work building");
    }

    private Lookup resolveColonyAndCitizen(
            Object colonyManager,
            Object level,
            int colonyId,
            int citizenId) {
        if (level == null) {
            return Lookup.failed("server level is required");
        }
        if (colonyId <= 0) {
            return Lookup.failed("colony id must be positive");
        }
        if (citizenId <= 0) {
            return Lookup.failed("citizen id must be positive");
        }

        Optional<Object> colony = ReflectiveHostAccess.invoke(
                colonyManager,
                "getColonyByWorld",
                colonyId,
                level);
        if (colony.isEmpty()) {
            return Lookup.failed("MineColonies colony " + colonyId + " was not found in this level");
        }
        OptionalInt resolvedColonyId = ReflectiveHostAccess.invokeInt(colony.get(), "getID");
        if (resolvedColonyId.isEmpty() || resolvedColonyId.getAsInt() != colonyId) {
            return Lookup.failed("resolved MineColonies colony id does not match " + colonyId);
        }

        Optional<Object> citizenManager = ReflectiveHostAccess.invokeNoArg(colony.get(), "getCitizenManager");
        if (citizenManager.isEmpty()) {
            return Lookup.failed("MineColonies colony " + colonyId + " has no citizen manager");
        }
        Optional<Object> citizenData = ReflectiveHostAccess.invoke(
                citizenManager.get(),
                "getCivilian",
                citizenId);
        if (citizenData.isEmpty()) {
            return Lookup.failed(
                    "MineColonies citizen " + citizenId + " was not found in colony " + colonyId);
        }

        return Lookup.resolved(citizenData.get());
    }

    private BuildingLookup buildingAt(
            Object colonyManager,
            Object level,
            int colonyId,
            Object position,
            String role) {
        if (position == null) {
            return BuildingLookup.failed(role + " building position is required");
        }

        Optional<Object> building = ReflectiveHostAccess.invoke(
                colonyManager,
                "getBuilding",
                level,
                position);
        if (building.isEmpty()) {
            return BuildingLookup.failed("MineColonies " + role + " building was not found at " + position);
        }

        OptionalInt ownerColonyId = ReflectiveHostAccess.invokeNoArg(building.get(), "getColony")
                .map(colony -> ReflectiveHostAccess.invokeInt(colony, "getID"))
                .orElseGet(OptionalInt::empty);
        if (ownerColonyId.isEmpty()) {
            return BuildingLookup.failed("MineColonies " + role + " building has no owning colony");
        }
        if (ownerColonyId.getAsInt() != colonyId) {
            return BuildingLookup.failed(
                    "MineColonies " + role + " building belongs to colony "
                            + ownerColonyId.getAsInt()
                            + ", not colony "
                            + colonyId);
        }

        return BuildingLookup.resolved(building.get());
    }

    private Optional<Object> colonyManager() {
        try {
            return colonyManagerSupplier.get();
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }

    private static Optional<Object> runtimeColonyManager() {
        try {
            Class<?> apiType = Class.forName(
                    MINECOLONIES_API,
                    false,
                    MineColoniesAssignmentTargetResolver.class.getClassLoader());
            Object api = apiType.getMethod("getInstance").invoke(null);
            return ReflectiveHostAccess.invokeNoArg(api, "getColonyManager");
        } catch (ClassNotFoundException
                 | IllegalAccessException
                 | InvocationTargetException
                 | NoSuchMethodException
                 | RuntimeException exception) {
            return Optional.empty();
        }
    }

    private record Lookup(boolean resolved, Object citizenData, String message) {
        private static Lookup resolved(Object citizenData) {
            return new Lookup(true, citizenData, "");
        }

        private static Lookup failed(String message) {
            return new Lookup(false, null, message);
        }
    }

    private record BuildingLookup(boolean resolved, Object building, String message) {
        private static BuildingLookup resolved(Object building) {
            return new BuildingLookup(true, building, "");
        }

        private static BuildingLookup failed(String message) {
            return new BuildingLookup(false, null, message);
        }
    }
}

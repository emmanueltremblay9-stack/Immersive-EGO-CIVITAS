package com.oblixorprime.immersiveego.civitas.resident.upstream;

public final class MineColoniesAssignmentCoordinator {
    private final MineColoniesAssignmentModuleLocator moduleLocator;
    private final MineColoniesAssignmentService assignmentService;

    public MineColoniesAssignmentCoordinator() {
        this(new MineColoniesAssignmentModuleLocator(), new MineColoniesAssignmentService());
    }

    MineColoniesAssignmentCoordinator(
            MineColoniesAssignmentModuleLocator moduleLocator,
            MineColoniesAssignmentService assignmentService) {
        this.moduleLocator = moduleLocator;
        this.assignmentService = assignmentService;
    }

    public MineColoniesAssignmentResult assignHomeOnly(
            Object citizenData,
            Object targetHomeBuilding) {
        MineColoniesAssignmentResolution resolution =
                moduleLocator.homeOnlyPlan(citizenData, targetHomeBuilding);
        if (!resolution.succeeded()) {
            return MineColoniesAssignmentResult.failed(resolution.message());
        }
        return assignmentService.execute(resolution.plan());
    }

    public MineColoniesAssignmentResult assignHomeAndWork(
            Object citizenData,
            Object targetHomeBuilding,
            Object targetWorkBuilding) {
        MineColoniesAssignmentResolution resolution =
                moduleLocator.homeAndWorkPlan(citizenData, targetHomeBuilding, targetWorkBuilding);
        if (!resolution.succeeded()) {
            return MineColoniesAssignmentResult.failed(resolution.message());
        }
        return assignmentService.execute(resolution.plan());
    }
}

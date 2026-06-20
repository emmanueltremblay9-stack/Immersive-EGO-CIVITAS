package com.oblixorprime.immersiveego.civitas.resident.upstream;

import java.util.Optional;

public final class MineColoniesAssignmentService {
    public MineColoniesAssignmentResult execute(MineColoniesAssignmentPlan plan) {
        ModuleChange homeChange = assignIfNeeded("home", plan.targetHomeModule(), plan.citizenData());
        if (!homeChange.succeeded()) {
            boolean homeChanged = homeProbablyChanged(plan);
            if (homeChanged) {
                RollbackResult rollback = rollback(plan, true, false);
                return MineColoniesAssignmentResult.failedAfterRollback(
                        true,
                        false,
                        rollback.complete(),
                        homeChange.message() + "; " + rollback.message());
            }
            return MineColoniesAssignmentResult.failed(homeChange.message());
        }

        ModuleChange workChange = assignIfNeeded("work", plan.targetWorkModule(), plan.citizenData());
        if (!workChange.succeeded()) {
            boolean workChanged = workProbablyChanged(plan);
            RollbackResult rollback = rollback(plan, homeChange.changed(), workChanged);
            return MineColoniesAssignmentResult.failedAfterRollback(
                    homeChange.changed(),
                    workChanged,
                    rollback.complete(),
                    workChange.message() + "; " + rollback.message());
        }

        return MineColoniesAssignmentResult.applied(
                homeChange.changed(),
                workChange.changed(),
                "MineColonies assignment applied through assignment modules");
    }

    private ModuleChange assignIfNeeded(String label, Object module, Object citizenData) {
        if (module == null) {
            return ModuleChange.skipped();
        }
        if (hasAssignedCitizen(module, citizenData)) {
            return ModuleChange.unchanged(label + " assignment already present");
        }
        Optional<Boolean> assigned = invokeBoolean(module, "assignCitizen", citizenData);
        if (assigned.orElse(false)) {
            return ModuleChange.changed(label + " assignment applied");
        }
        return ModuleChange.failed(label + " assignment module rejected citizen");
    }

    private RollbackResult rollback(MineColoniesAssignmentPlan plan, boolean homeChanged, boolean workChanged) {
        boolean complete = true;
        StringBuilder message = new StringBuilder("rollback attempted");

        if (workChanged) {
            complete &= removeFromTarget("work", plan.targetWorkModule(), plan.citizenData(), message);
            complete &= restorePrevious("work", plan.previousWorkModule(), plan.citizenData(), message);
        }

        if (homeChanged) {
            complete &= removeFromTarget("home", plan.targetHomeModule(), plan.citizenData(), message);
            complete &= restorePrevious("home", plan.previousHomeModule(), plan.citizenData(), message);
        }

        return new RollbackResult(complete, message.toString());
    }

    private boolean removeFromTarget(String label, Object module, Object citizenData, StringBuilder message) {
        if (module == null || !hasAssignedCitizen(module, citizenData)) {
            return true;
        }
        boolean removed = invokeBoolean(module, "removeCitizen", citizenData).orElse(false);
        message.append("; ").append(label).append(" target removed=").append(removed);
        return removed;
    }

    private boolean restorePrevious(String label, Object previousModule, Object citizenData, StringBuilder message) {
        if (previousModule == null || hasAssignedCitizen(previousModule, citizenData)) {
            return true;
        }
        boolean restored = invokeBoolean(previousModule, "assignCitizen", citizenData).orElse(false);
        message.append("; ").append(label).append(" previous restored=").append(restored);
        return restored;
    }

    private boolean workProbablyChanged(MineColoniesAssignmentPlan plan) {
        return plan.targetWorkModule() != null
                && hasAssignedCitizen(plan.targetWorkModule(), plan.citizenData());
    }

    private boolean homeProbablyChanged(MineColoniesAssignmentPlan plan) {
        return plan.targetHomeModule() != null
                && hasAssignedCitizen(plan.targetHomeModule(), plan.citizenData());
    }

    private boolean hasAssignedCitizen(Object module, Object citizenData) {
        return invokeBoolean(module, "hasAssignedCitizen", citizenData).orElse(false);
    }

    private Optional<Boolean> invokeBoolean(Object target, String methodName, Object... arguments) {
        return ReflectiveHostAccess.invoke(target, methodName, arguments)
                .filter(Boolean.class::isInstance)
                .map(Boolean.class::cast);
    }

    private record ModuleChange(boolean succeeded, boolean changed, String message) {
        static ModuleChange skipped() {
            return new ModuleChange(true, false, "assignment skipped");
        }

        static ModuleChange unchanged(String message) {
            return new ModuleChange(true, false, message);
        }

        static ModuleChange changed(String message) {
            return new ModuleChange(true, true, message);
        }

        static ModuleChange failed(String message) {
            return new ModuleChange(false, false, message);
        }
    }

    private record RollbackResult(boolean complete, String message) {
    }
}

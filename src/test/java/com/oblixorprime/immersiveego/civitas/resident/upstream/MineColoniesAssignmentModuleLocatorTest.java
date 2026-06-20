package com.oblixorprime.immersiveego.civitas.resident.upstream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MineColoniesAssignmentModuleLocatorTest {
    private final MineColoniesAssignmentModuleLocator locator =
            new MineColoniesAssignmentModuleLocator(FakeAssignsCitizen.class, FakeAssignsJob.class);

    @Test
    void homePlanSelectsLivingModuleAndIgnoresWorkerModules() {
        FakeCitizenData citizen = new FakeCitizenData();
        FakeBuilding previousHome = new FakeBuilding();
        FakeHomeModule previousHomeModule = new FakeHomeModule();
        FakeBuilding targetHome = new FakeBuilding();
        FakeHomeModule targetHomeModule = new FakeHomeModule();
        FakeWorkModule workerModule = new FakeWorkModule();
        previousHomeModule.assignCitizen(citizen);
        previousHome.modules.add(previousHomeModule);
        targetHome.modules.add(workerModule);
        targetHome.modules.add(targetHomeModule);
        citizen.homeBuilding = previousHome;

        MineColoniesAssignmentResolution resolution = locator.homeOnlyPlan(citizen, targetHome);

        assertTrue(resolution.succeeded());
        assertSame(targetHomeModule, resolution.plan().targetHomeModule());
        assertSame(previousHomeModule, resolution.plan().previousHomeModule());
    }

    @Test
    void workPlanUsesJobWorkModuleAsRollbackTarget() {
        FakeCitizenData citizen = new FakeCitizenData();
        FakeWorkModule previousWorkModule = new FakeWorkModule();
        FakeBuilding targetWork = new FakeBuilding();
        FakeWorkModule targetWorkModule = new FakeWorkModule();
        previousWorkModule.assignCitizen(citizen);
        citizen.job = new FakeJob(previousWorkModule);
        targetWork.modules.add(targetWorkModule);

        MineColoniesAssignmentResolution resolution =
                locator.homeAndWorkPlan(citizen, null, targetWork);

        assertTrue(resolution.succeeded());
        assertSame(targetWorkModule, resolution.plan().targetWorkModule());
        assertSame(previousWorkModule, resolution.plan().previousWorkModule());
    }

    @Test
    void targetSelectionRejectsAmbiguousOpenHomeModules() {
        FakeCitizenData citizen = new FakeCitizenData();
        FakeBuilding targetHome = new FakeBuilding();
        targetHome.modules.add(new FakeHomeModule());
        targetHome.modules.add(new FakeHomeModule());

        MineColoniesAssignmentResolution resolution = locator.homeOnlyPlan(citizen, targetHome);

        assertFalse(resolution.succeeded());
        assertTrue(resolution.message().contains("multiple open MineColonies home"));
    }

    @Test
    void coordinatorRoutesDiscoveredModulesThroughRepairableExecutor() {
        FakeCitizenData citizen = new FakeCitizenData();
        FakeBuilding previousHome = new FakeBuilding();
        FakeHomeModule previousHomeModule = new FakeHomeModule();
        FakeBuilding targetHome = new FakeBuilding();
        FakeHomeModule targetHomeModule = new FakeHomeModule();
        FakeBuilding targetWork = new FakeBuilding();
        FakeWorkModule rejectingWork = new FakeWorkModule(false);
        previousHomeModule.assignCitizen(citizen);
        previousHome.modules.add(previousHomeModule);
        targetHome.modules.add(targetHomeModule);
        targetWork.modules.add(rejectingWork);
        citizen.homeBuilding = previousHome;

        MineColoniesAssignmentCoordinator coordinator =
                new MineColoniesAssignmentCoordinator(locator, new MineColoniesAssignmentService());
        MineColoniesAssignmentResult result =
                coordinator.assignHomeAndWork(citizen, targetHome, targetWork);

        assertFalse(result.succeeded());
        assertTrue(result.rollbackAttempted());
        assertTrue(result.rollbackComplete());
        assertFalse(targetHomeModule.hasAssignedCitizen(citizen));
        assertTrue(previousHomeModule.hasAssignedCitizen(citizen));
        assertFalse(rejectingWork.hasAssignedCitizen(citizen));
    }

    private interface FakeAssignsCitizen {
    }

    private interface FakeAssignsJob extends FakeAssignsCitizen {
    }

    private static final class FakeCitizenData {
        private FakeBuilding homeBuilding;
        private FakeJob job;

        public Object getHomeBuilding() {
            return homeBuilding;
        }

        public Object getJob() {
            return job;
        }
    }

    private record FakeJob(Object workModule) {
        public Object getWorkModule() {
            return workModule;
        }
    }

    private static final class FakeBuilding {
        private final List<Object> modules = new ArrayList<>();

        public <T> List<T> getModulesByType(Class<T> type) {
            return modules.stream()
                    .filter(type::isInstance)
                    .map(type::cast)
                    .toList();
        }
    }

    private static class FakeAssignmentModule implements FakeAssignsCitizen {
        private final Set<Object> assigned = new LinkedHashSet<>();
        private final boolean assignmentResult;

        FakeAssignmentModule() {
            this(true);
        }

        FakeAssignmentModule(boolean assignmentResult) {
            this.assignmentResult = assignmentResult;
        }

        public boolean assignCitizen(Object citizenData) {
            if (assignmentResult) {
                assigned.add(citizenData);
            }
            return assignmentResult;
        }

        public boolean removeCitizen(Object citizenData) {
            assigned.remove(citizenData);
            return true;
        }

        public boolean hasAssignedCitizen(Object citizenData) {
            return assigned.contains(citizenData);
        }

        public boolean isFull() {
            return false;
        }
    }

    private static final class FakeHomeModule extends FakeAssignmentModule {
    }

    private static final class FakeWorkModule extends FakeAssignmentModule implements FakeAssignsJob {
        FakeWorkModule() {
            super();
        }

        FakeWorkModule(boolean assignmentResult) {
            super(assignmentResult);
        }
    }
}

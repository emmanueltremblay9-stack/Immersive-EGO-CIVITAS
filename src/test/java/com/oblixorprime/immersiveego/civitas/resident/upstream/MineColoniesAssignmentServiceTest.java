package com.oblixorprime.immersiveego.civitas.resident.upstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MineColoniesAssignmentServiceTest {
    private final MineColoniesAssignmentService service = new MineColoniesAssignmentService();

    @Test
    void assignmentExecutesHomeAndWorkThroughModules() {
        FakeCitizenData citizen = new FakeCitizenData(42);
        FakeAssignmentModule home = new FakeAssignmentModule();
        FakeAssignmentModule work = new FakeAssignmentModule();

        MineColoniesAssignmentResult result = service.execute(
                MineColoniesAssignmentPlan.homeAndWork(citizen, home, null, work, null));

        assertTrue(result.succeeded());
        assertTrue(result.homeChanged());
        assertTrue(result.workChanged());
        assertFalse(result.rollbackAttempted());
        assertTrue(home.hasAssignedCitizen(citizen));
        assertTrue(work.hasAssignedCitizen(citizen));
        assertEquals(1, home.assignCalls);
        assertEquals(1, work.assignCalls);
    }

    @Test
    void assignmentRollsHomeBackWhenWorkRejectsCitizen() {
        FakeCitizenData citizen = new FakeCitizenData(42);
        FakeAssignmentModule previousHome = new FakeAssignmentModule();
        FakeAssignmentModule targetHome = new FakeAssignmentModule();
        FakeAssignmentModule rejectingWork = new FakeAssignmentModule(false, false);
        previousHome.assignCitizen(citizen);

        MineColoniesAssignmentResult result = service.execute(
                MineColoniesAssignmentPlan.homeAndWork(
                        citizen,
                        targetHome,
                        previousHome,
                        rejectingWork,
                        null));

        assertFalse(result.succeeded());
        assertTrue(result.rollbackAttempted());
        assertTrue(result.rollbackComplete());
        assertFalse(result.workChanged());
        assertFalse(targetHome.hasAssignedCitizen(citizen));
        assertTrue(previousHome.hasAssignedCitizen(citizen));
        assertFalse(rejectingWork.hasAssignedCitizen(citizen));
    }

    @Test
    void assignmentLeavesExistingHomeInPlaceWhenWorkRejectsCitizen() {
        FakeCitizenData citizen = new FakeCitizenData(42);
        FakeAssignmentModule currentHome = new FakeAssignmentModule();
        FakeAssignmentModule rejectingWork = new FakeAssignmentModule(false, false);
        currentHome.assignCitizen(citizen);

        MineColoniesAssignmentResult result = service.execute(
                MineColoniesAssignmentPlan.homeAndWork(
                        citizen,
                        currentHome,
                        null,
                        rejectingWork,
                        null));

        assertFalse(result.succeeded());
        assertTrue(result.rollbackAttempted());
        assertTrue(result.rollbackComplete());
        assertTrue(currentHome.hasAssignedCitizen(citizen));
        assertEquals(1, currentHome.assignCalls);
    }

    @Test
    void assignmentRollsPartialWorkMutationBackWhenMethodThrows() {
        FakeCitizenData citizen = new FakeCitizenData(42);
        FakeAssignmentModule targetHome = new FakeAssignmentModule();
        FakeAssignmentModule previousWork = new FakeAssignmentModule();
        FakeAssignmentModule throwingWork = new FakeAssignmentModule(true, true);
        previousWork.assignCitizen(citizen);

        MineColoniesAssignmentResult result = service.execute(
                MineColoniesAssignmentPlan.homeAndWork(
                        citizen,
                        targetHome,
                        null,
                        throwingWork,
                        previousWork));

        assertFalse(result.succeeded());
        assertTrue(result.rollbackAttempted());
        assertTrue(result.rollbackComplete());
        assertTrue(result.workChanged());
        assertFalse(targetHome.hasAssignedCitizen(citizen));
        assertFalse(throwingWork.hasAssignedCitizen(citizen));
        assertTrue(previousWork.hasAssignedCitizen(citizen));
    }

    @Test
    void assignmentRollsPartialHomeMutationBackWhenMethodThrows() {
        FakeCitizenData citizen = new FakeCitizenData(42);
        FakeAssignmentModule previousHome = new FakeAssignmentModule();
        FakeAssignmentModule throwingHome = new FakeAssignmentModule(true, true);
        previousHome.assignCitizen(citizen);

        MineColoniesAssignmentResult result = service.execute(
                MineColoniesAssignmentPlan.homeOnly(citizen, throwingHome, previousHome));

        assertFalse(result.succeeded());
        assertTrue(result.rollbackAttempted());
        assertTrue(result.rollbackComplete());
        assertTrue(result.homeChanged());
        assertFalse(throwingHome.hasAssignedCitizen(citizen));
        assertTrue(previousHome.hasAssignedCitizen(citizen));
    }

    @Test
    void assignmentPlanRejectsInvalidInputs() {
        FakeCitizenData citizen = new FakeCitizenData(42);

        assertThrows(IllegalArgumentException.class, () ->
                MineColoniesAssignmentPlan.homeOnly(null, new FakeAssignmentModule(), null));
        assertThrows(IllegalArgumentException.class, () ->
                MineColoniesAssignmentPlan.homeAndWork(citizen, null, null, null, null));
    }

    private record FakeCitizenData(int citizenId) {
    }

    private static final class FakeAssignmentModule {
        private final Set<Object> assigned = new LinkedHashSet<>();
        private final boolean throwAfterMutation;
        private final boolean assignmentResult;
        private int assignCalls;

        private FakeAssignmentModule() {
            this(false, true);
        }

        private FakeAssignmentModule(boolean throwAfterMutation, boolean assignmentResult) {
            this.throwAfterMutation = throwAfterMutation;
            this.assignmentResult = assignmentResult;
        }

        public boolean assignCitizen(Object citizenData) {
            assignCalls++;
            if (assignmentResult) {
                assigned.add(citizenData);
            }
            if (throwAfterMutation) {
                throw new IllegalStateException("simulated partial mutation");
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
    }
}

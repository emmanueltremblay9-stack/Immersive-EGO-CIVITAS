package com.oblixorprime.immersiveego.civitas.ego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;

class CivitasEgoFacadeTest {
    @Test
    void stateReaderBuildsBoundedReadinessSnapshot() {
        record FakeEgoState(
                double readiness,
                double hydration,
                double nutritionBalance,
                double stamina,
                double fatigue,
                double stress,
                double thermalStrain,
                double respiratoryStrain,
                double recoveryCapacity) {
        }

        CivitasEgoSnapshot snapshot = CivitasEgoStateReader.readSnapshot(new FakeEgoState(
                        1.25D,
                        0.60D,
                        0.70D,
                        0.80D,
                        -0.25D,
                        0.20D,
                        0.10D,
                        0.15D,
                        0.90D))
                .orElseThrow();

        assertEquals(1.0D, snapshot.readiness());
        assertEquals(0.0D, snapshot.fatigue());
        assertTrue(snapshot.canWork());
        assertTrue(snapshot.canFight());
        assertTrue(snapshot.canTravel());
        assertTrue(snapshot.canRecover());
        assertEquals(OptionalDouble.empty(), snapshot.sleepPressure());
        assertTrue(snapshot.originId().isEmpty());
    }

    @Test
    void stateReaderFailsClosedWhenRequiredAccessorsAreMissing() {
        record IncompleteEgoState(double readiness) {
        }

        assertTrue(CivitasEgoStateReader.readSnapshot(new IncompleteEgoState(1.0D)).isEmpty());
    }

    @Test
    void contractStatusSeparatesStateShapeFromMissingNpcSubjectApi() {
        CivitasEgoApiStatus status = CivitasEgoApiContract.inspect(getClass().getClassLoader());

        assertFalse(status.npcSubjectApiAvailable());
        assertFalse(status.readyForNpcReadinessIntegration());
        assertTrue(status.missingRequirements().stream()
                .anyMatch(requirement -> requirement.contains("NPC subject")));
    }
}

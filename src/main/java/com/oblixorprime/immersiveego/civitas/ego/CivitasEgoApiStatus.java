package com.oblixorprime.immersiveego.civitas.ego;

import java.util.List;

public record CivitasEgoApiStatus(
        boolean stateSnapshotAvailable,
        boolean npcSubjectApiAvailable,
        List<String> missingMembers,
        List<String> missingRequirements) {
    public CivitasEgoApiStatus {
        missingMembers = List.copyOf(missingMembers);
        missingRequirements = List.copyOf(missingRequirements);
    }

    public boolean readyForNpcReadinessIntegration() {
        return stateSnapshotAvailable && npcSubjectApiAvailable;
    }
}

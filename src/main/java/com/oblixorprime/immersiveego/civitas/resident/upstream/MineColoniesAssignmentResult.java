package com.oblixorprime.immersiveego.civitas.resident.upstream;

public record MineColoniesAssignmentResult(
        boolean succeeded,
        boolean homeChanged,
        boolean workChanged,
        boolean rollbackAttempted,
        boolean rollbackComplete,
        String message) {
    public static MineColoniesAssignmentResult applied(
            boolean homeChanged,
            boolean workChanged,
            String message) {
        return new MineColoniesAssignmentResult(true, homeChanged, workChanged, false, false, message);
    }

    public static MineColoniesAssignmentResult failed(String message) {
        return new MineColoniesAssignmentResult(false, false, false, false, false, message);
    }

    public static MineColoniesAssignmentResult failedAfterRollback(
            boolean homeChanged,
            boolean workChanged,
            boolean rollbackComplete,
            String message) {
        return new MineColoniesAssignmentResult(false, homeChanged, workChanged, true, rollbackComplete, message);
    }
}

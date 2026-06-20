package com.oblixorprime.immersiveego.civitas.resident.upstream;

public record MineColoniesAssignmentResolution(MineColoniesAssignmentPlan plan, String message) {
    public MineColoniesAssignmentResolution {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is required");
        }
    }

    public static MineColoniesAssignmentResolution resolved(
            MineColoniesAssignmentPlan plan,
            String message) {
        if (plan == null) {
            throw new IllegalArgumentException("plan is required");
        }
        return new MineColoniesAssignmentResolution(plan, message);
    }

    public static MineColoniesAssignmentResolution failed(String message) {
        return new MineColoniesAssignmentResolution(null, message);
    }

    public boolean succeeded() {
        return plan != null;
    }
}

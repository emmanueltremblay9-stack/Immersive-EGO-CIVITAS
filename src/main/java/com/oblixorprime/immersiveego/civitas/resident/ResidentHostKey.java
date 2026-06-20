package com.oblixorprime.immersiveego.civitas.resident;

public record ResidentHostKey(CivitasAuthority authority, String hostId) {
    public ResidentHostKey {
        if (authority == null) {
            throw new IllegalArgumentException("authority is required");
        }
        if (hostId == null || hostId.isBlank()) {
            throw new IllegalArgumentException("hostId is required");
        }
        hostId = hostId.trim();
    }
}

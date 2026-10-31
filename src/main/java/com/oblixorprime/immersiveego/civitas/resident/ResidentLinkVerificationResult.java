package com.oblixorprime.immersiveego.civitas.resident;

import java.util.Optional;
import java.util.UUID;

public record ResidentLinkVerificationResult(
        boolean verified,
        Optional<UUID> residentId,
        Optional<ResidentHostKey> mcaHost,
        Optional<ResidentHostKey> mineColoniesHost,
        String message) {
    public ResidentLinkVerificationResult {
        residentId = residentId == null ? Optional.empty() : residentId;
        mcaHost = mcaHost == null ? Optional.empty() : mcaHost;
        mineColoniesHost = mineColoniesHost == null ? Optional.empty() : mineColoniesHost;
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("verification message is required");
        }
        message = message.trim();
    }

    public static ResidentLinkVerificationResult verified(
            ResidentRecord resident,
            ResidentHostKey mcaHost,
            ResidentHostKey mineColoniesHost) {
        return new ResidentLinkVerificationResult(
                true,
                Optional.of(resident.residentId()),
                Optional.of(mcaHost),
                Optional.of(mineColoniesHost),
                "MCA and MineColonies hosts resolve to one CIVITAS resident");
    }

    public static ResidentLinkVerificationResult failed(String message) {
        return new ResidentLinkVerificationResult(
                false,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                message);
    }
}

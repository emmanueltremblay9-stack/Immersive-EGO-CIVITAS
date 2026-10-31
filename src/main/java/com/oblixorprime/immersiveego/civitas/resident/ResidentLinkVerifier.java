package com.oblixorprime.immersiveego.civitas.resident;

import java.util.List;
import java.util.Objects;

public final class ResidentLinkVerifier {
    private final ResidentHostAdapterRegistry adapters;

    public ResidentLinkVerifier(ResidentHostAdapterRegistry adapters) {
        this.adapters = Objects.requireNonNull(adapters, "adapters");
    }

    public ResidentLinkVerificationResult verifyMcaMineColoniesLink(
            ResidentDirectory residents,
            Object mcaHost,
            Object mineColoniesHost) {
        Objects.requireNonNull(residents, "residents");
        try {
            ResidentHostKey mcaHostKey = requireHostKey(mcaHost, CivitasAuthority.MCA_REBORN);
            ResidentHostKey mineColoniesHostKey = requireHostKey(
                    mineColoniesHost,
                    CivitasAuthority.MINECOLONIES);

            ResidentRecord mcaResident = residents.find(mcaHostKey)
                    .orElse(null);
            if (mcaResident == null) {
                return ResidentLinkVerificationResult.failed(
                        "No CIVITAS resident is linked to MCA host " + mcaHostKey.hostId());
            }

            ResidentRecord mineColoniesResident = residents.find(mineColoniesHostKey)
                    .orElse(null);
            if (mineColoniesResident == null) {
                return ResidentLinkVerificationResult.failed(
                        "No CIVITAS resident is linked to MineColonies host "
                                + mineColoniesHostKey.hostId());
            }

            if (!mcaResident.residentId().equals(mineColoniesResident.residentId())) {
                return ResidentLinkVerificationResult.failed(
                        "MCA host " + mcaHostKey.hostId()
                                + " resolves to resident " + mcaResident.residentId()
                                + " but MineColonies host " + mineColoniesHostKey.hostId()
                                + " resolves to resident " + mineColoniesResident.residentId());
            }

            ResidentRecord resident = mcaResident;
            if (resident.host(CivitasAuthority.MCA_REBORN).filter(mcaHostKey::equals).isEmpty()
                    || resident.host(CivitasAuthority.MINECOLONIES).filter(mineColoniesHostKey::equals).isEmpty()) {
                return ResidentLinkVerificationResult.failed(
                        "Resolved CIVITAS resident is missing one exact upstream host link");
            }

            return ResidentLinkVerificationResult.verified(
                    resident,
                    mcaHostKey,
                    mineColoniesHostKey);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ResidentLinkVerificationResult.failed(exception.getMessage());
        }
    }

    private ResidentHostKey requireHostKey(Object host, CivitasAuthority authority) {
        Objects.requireNonNull(authority, "authority");
        if (host == null) {
            throw new IllegalArgumentException(authorityDisplayName(authority) + " host is required");
        }
        List<ResidentHostKey> hostKeys = adapters.identify(host);
        if (hostKeys.isEmpty()) {
            throw new IllegalArgumentException(
                    "No CIVITAS resident host adapter identified " + host.getClass().getName());
        }

        return hostKeys.stream()
                .filter(hostKey -> hostKey.authority() == authority)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Host " + host.getClass().getName() + " did not identify as "
                                + authority.serializedId()));
    }

    private static String authorityDisplayName(CivitasAuthority authority) {
        return switch (authority) {
            case MCA_REBORN -> "MCA Reborn";
            case MINECOLONIES -> "MineColonies";
            case IMMERSIVE_EGO -> "Immersive EGO";
            case MODERN_COMPANIONS -> "Modern Companions";
            case CIVITAS -> "CIVITAS";
        };
    }
}

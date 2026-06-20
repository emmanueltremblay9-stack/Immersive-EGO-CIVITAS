package com.oblixorprime.immersiveego.civitas.resident;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public final class ResidentRecruitmentService {
    private final ResidentIdentityService identities;

    public ResidentRecruitmentService(ResidentIdentityService identities) {
        this.identities = identities;
    }

    public ResidentRecruitmentResult recruitMcaIntoColony(
            Object mcaHost,
            Object mineColoniesHost,
            long gameTime,
            Supplier<UUID> residentIdSupplier) {
        ResidentHostKey mcaHostKey = identities.requireHostKey(mcaHost, CivitasAuthority.MCA_REBORN);
        ResidentHostKey mineColoniesHostKey = identities.requireHostKey(
                mineColoniesHost,
                CivitasAuthority.MINECOLONIES);
        ResidentRecord resident = identities.getOrCreateKeys(
                List.of(mcaHostKey, mineColoniesHostKey),
                gameTime,
                residentIdSupplier);

        return new ResidentRecruitmentResult(
                resident,
                requireHost(resident, mcaHostKey),
                requireHost(resident, mineColoniesHostKey));
    }

    private ResidentHostKey requireHost(ResidentRecord resident, ResidentHostKey expectedHost) {
        return resident.host(expectedHost.authority())
                .filter(expectedHost::equals)
                .orElseThrow(() -> new IllegalStateException("Recruitment did not link " + expectedHost));
    }
}

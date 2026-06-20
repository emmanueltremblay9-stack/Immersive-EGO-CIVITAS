package com.oblixorprime.immersiveego.civitas.resident;

public record ResidentRecruitmentResult(
        ResidentRecord resident,
        ResidentHostKey mcaHost,
        ResidentHostKey mineColoniesHost) {
    public ResidentRecruitmentResult {
        if (resident == null) {
            throw new IllegalArgumentException("resident is required");
        }
        if (mcaHost == null || mcaHost.authority() != CivitasAuthority.MCA_REBORN) {
            throw new IllegalArgumentException("MCA host is required");
        }
        if (mineColoniesHost == null || mineColoniesHost.authority() != CivitasAuthority.MINECOLONIES) {
            throw new IllegalArgumentException("MineColonies host is required");
        }
    }
}

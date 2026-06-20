package com.oblixorprime.immersiveego.civitas.resident;

import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentCoordinator;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentGateway;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentResult;
import com.oblixorprime.immersiveego.civitas.resident.upstream.UpstreamResidentHostAdapters;
import java.util.Objects;
import java.util.Optional;

public final class LinkedResidentAssignmentService {
    private final ResidentHostAdapterRegistry adapters;
    private final MineColoniesAssignmentGateway assignmentGateway;

    public LinkedResidentAssignmentService() {
        this(UpstreamResidentHostAdapters.createRegistry(), new MineColoniesAssignmentCoordinator());
    }

    public LinkedResidentAssignmentService(
            ResidentHostAdapterRegistry adapters,
            MineColoniesAssignmentGateway assignmentGateway) {
        this.adapters = Objects.requireNonNull(adapters, "adapters");
        this.assignmentGateway = Objects.requireNonNull(assignmentGateway, "assignmentGateway");
    }

    public MineColoniesAssignmentResult assignHomeOnly(
            ResidentDirectory residents,
            Object mineColoniesCitizenData,
            Object targetHomeBuilding) {
        Optional<MineColoniesAssignmentResult> guard =
                rejectUnlessLinked(residents, mineColoniesCitizenData);
        if (guard.isPresent()) {
            return guard.get();
        }
        return assignmentGateway.assignHomeOnly(mineColoniesCitizenData, targetHomeBuilding);
    }

    public MineColoniesAssignmentResult assignHomeAndWork(
            ResidentDirectory residents,
            Object mineColoniesCitizenData,
            Object targetHomeBuilding,
            Object targetWorkBuilding) {
        Optional<MineColoniesAssignmentResult> guard =
                rejectUnlessLinked(residents, mineColoniesCitizenData);
        if (guard.isPresent()) {
            return guard.get();
        }
        return assignmentGateway.assignHomeAndWork(
                mineColoniesCitizenData,
                targetHomeBuilding,
                targetWorkBuilding);
    }

    private Optional<MineColoniesAssignmentResult> rejectUnlessLinked(
            ResidentDirectory residents,
            Object mineColoniesCitizenData) {
        if (residents == null) {
            return Optional.of(MineColoniesAssignmentResult.failed("resident saved data is required"));
        }
        if (mineColoniesCitizenData == null) {
            return Optional.of(MineColoniesAssignmentResult.failed("MineColonies citizen data is required"));
        }

        Optional<ResidentHostKey> mineColoniesHost = adapters.identify(mineColoniesCitizenData).stream()
                .filter(hostKey -> hostKey.authority() == CivitasAuthority.MINECOLONIES)
                .findFirst();
        if (mineColoniesHost.isEmpty()) {
            return Optional.of(MineColoniesAssignmentResult.failed(
                    "host did not resolve to a MineColonies CIVITAS resident key"));
        }

        Optional<ResidentRecord> resident = residents.find(mineColoniesHost.get());
        if (resident.isEmpty()) {
            return Optional.of(MineColoniesAssignmentResult.failed(
                    "MineColonies citizen is not linked to a CIVITAS resident"));
        }

        if (resident.get().host(CivitasAuthority.MCA_REBORN).isEmpty()) {
            return Optional.of(MineColoniesAssignmentResult.failed(
                    "CIVITAS resident is missing an MCA Reborn host link"));
        }

        return Optional.empty();
    }
}

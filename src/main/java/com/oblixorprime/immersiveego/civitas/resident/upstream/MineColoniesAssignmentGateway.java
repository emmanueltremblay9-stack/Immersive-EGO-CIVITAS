package com.oblixorprime.immersiveego.civitas.resident.upstream;

public interface MineColoniesAssignmentGateway {
    MineColoniesAssignmentResult assignHomeOnly(Object citizenData, Object targetHomeBuilding);

    MineColoniesAssignmentResult assignHomeAndWork(
            Object citizenData,
            Object targetHomeBuilding,
            Object targetWorkBuilding);
}

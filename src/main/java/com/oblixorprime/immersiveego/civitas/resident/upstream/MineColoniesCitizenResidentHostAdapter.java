package com.oblixorprime.immersiveego.civitas.resident.upstream;

import com.oblixorprime.immersiveego.civitas.resident.CivitasAuthority;
import com.oblixorprime.immersiveego.civitas.resident.ResidentHostAdapter;
import com.oblixorprime.immersiveego.civitas.resident.ResidentHostKey;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

public final class MineColoniesCitizenResidentHostAdapter implements ResidentHostAdapter<Object> {
    static final String ABSTRACT_CITIZEN_CLASS =
            "com.minecolonies.api.entity.citizen.AbstractEntityCitizen";
    static final String CITIZEN_DATA_INTERFACE = "com.minecolonies.api.colony.ICitizenData";
    static final String CITIZEN_INTERFACE = "com.minecolonies.api.colony.ICitizen";

    private final Set<String> entityTypeNames;
    private final Set<String> citizenTypeNames;

    public MineColoniesCitizenResidentHostAdapter() {
        this(
                Set.of(ABSTRACT_CITIZEN_CLASS),
                Set.of(CITIZEN_DATA_INTERFACE, CITIZEN_INTERFACE));
    }

    MineColoniesCitizenResidentHostAdapter(Set<String> entityTypeNames, Set<String> citizenTypeNames) {
        this.entityTypeNames = Set.copyOf(entityTypeNames);
        this.citizenTypeNames = Set.copyOf(citizenTypeNames);
    }

    @Override
    public CivitasAuthority authority() {
        return CivitasAuthority.MINECOLONIES;
    }

    @Override
    public Class<Object> hostType() {
        return Object.class;
    }

    @Override
    public Optional<ResidentHostKey> identify(Object host) {
        Optional<ResidentHostKey> dataKey = citizenData(host).flatMap(this::identifyCitizenData);
        if (dataKey.isPresent()) {
            return dataKey;
        }

        if (!ReflectiveHostAccess.hasTypeName(host, entityTypeNames)) {
            return Optional.empty();
        }
        return identifyCitizenEntity(host);
    }

    private Optional<Object> citizenData(Object host) {
        if (ReflectiveHostAccess.hasTypeName(host, citizenTypeNames)) {
            return Optional.of(host);
        }
        return ReflectiveHostAccess.invokeNoArg(host, "getCitizenData")
                .filter(candidate -> ReflectiveHostAccess.hasTypeName(candidate, citizenTypeNames));
    }

    private Optional<ResidentHostKey> identifyCitizenData(Object citizenData) {
        OptionalInt citizenId = ReflectiveHostAccess.invokeInt(citizenData, "getId");
        OptionalInt colonyId = ReflectiveHostAccess.invokeNoArg(citizenData, "getColony")
                .map(colony -> ReflectiveHostAccess.invokeInt(colony, "getID"))
                .orElseGet(OptionalInt::empty);
        return hostKey(colonyId, citizenId);
    }

    private Optional<ResidentHostKey> identifyCitizenEntity(Object entity) {
        OptionalInt citizenId = ReflectiveHostAccess.invokeInt(entity, "getCivilianID");
        OptionalInt colonyId = ReflectiveHostAccess.invokeNoArg(entity, "getCitizenColonyHandler")
                .map(handler -> ReflectiveHostAccess.invokeInt(handler, "getColonyId"))
                .orElseGet(OptionalInt::empty);
        return hostKey(colonyId, citizenId);
    }

    private Optional<ResidentHostKey> hostKey(OptionalInt colonyId, OptionalInt citizenId) {
        if (colonyId.isEmpty() || citizenId.isEmpty() || colonyId.getAsInt() <= 0 || citizenId.getAsInt() <= 0) {
            return Optional.empty();
        }
        return Optional.of(new ResidentHostKey(
                authority(),
                "colony:" + colonyId.getAsInt() + "/citizen:" + citizenId.getAsInt()));
    }
}

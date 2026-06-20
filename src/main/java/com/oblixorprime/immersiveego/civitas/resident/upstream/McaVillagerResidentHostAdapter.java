package com.oblixorprime.immersiveego.civitas.resident.upstream;

import com.oblixorprime.immersiveego.civitas.resident.CivitasAuthority;
import com.oblixorprime.immersiveego.civitas.resident.ResidentHostAdapter;
import com.oblixorprime.immersiveego.civitas.resident.ResidentHostKey;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class McaVillagerResidentHostAdapter implements ResidentHostAdapter<Object> {
    static final String VILLAGER_ENTITY_CLASS = "net.conczin.mca.entity.VillagerEntityMCA";
    static final String VILLAGER_LIKE_INTERFACE = "net.conczin.mca.entity.VillagerLike";
    private static final String HOST_PREFIX = "villager_entity:";

    private final Set<String> acceptedTypeNames;

    public McaVillagerResidentHostAdapter() {
        this(Set.of(VILLAGER_ENTITY_CLASS, VILLAGER_LIKE_INTERFACE));
    }

    McaVillagerResidentHostAdapter(Set<String> acceptedTypeNames) {
        this.acceptedTypeNames = Set.copyOf(acceptedTypeNames);
    }

    @Override
    public CivitasAuthority authority() {
        return CivitasAuthority.MCA_REBORN;
    }

    @Override
    public Class<Object> hostType() {
        return Object.class;
    }

    @Override
    public Optional<ResidentHostKey> identify(Object host) {
        if (!ReflectiveHostAccess.hasTypeName(host, acceptedTypeNames)) {
            return Optional.empty();
        }

        return ReflectiveHostAccess.invokeUuid(host, "getUUID")
                .map(this::hostKey);
    }

    private ResidentHostKey hostKey(UUID entityId) {
        return new ResidentHostKey(authority(), HOST_PREFIX + entityId);
    }
}

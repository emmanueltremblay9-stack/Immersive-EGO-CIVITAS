package com.oblixorprime.immersiveego.civitas.resident;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public final class ResidentIdentityService {
    private final ResidentRegistry registry;
    private final ResidentHostAdapterRegistry adapters;

    public ResidentIdentityService(ResidentRegistry registry, ResidentHostAdapterRegistry adapters) {
        this.registry = registry;
        this.adapters = adapters;
    }

    public Optional<ResidentRecord> find(Object host) {
        List<ResidentHostKey> hostKeys = requireHostKeys(host);
        return hostKeys.stream()
                .map(registry::find)
                .flatMap(Optional::stream)
                .findFirst();
    }

    public ResidentRecord getOrCreate(Object host, long gameTime, Supplier<UUID> residentIdSupplier) {
        List<ResidentHostKey> hostKeys = requireHostKeys(host);
        ResidentRecord resident = resolveExistingResident(hostKeys)
                .orElseGet(() -> registry.getOrCreate(hostKeys.getFirst(), gameTime, residentIdSupplier));

        for (ResidentHostKey hostKey : hostKeys) {
            if (registry.find(hostKey).isEmpty()) {
                resident = registry.linkHost(resident.residentId(), hostKey, gameTime);
            }
        }
        return resident;
    }

    private List<ResidentHostKey> requireHostKeys(Object host) {
        List<ResidentHostKey> hostKeys = adapters.identify(host);
        if (hostKeys.isEmpty()) {
            throw new IllegalArgumentException(
                    "No CIVITAS resident host adapter identified " + host.getClass().getName());
        }
        return hostKeys;
    }

    private Optional<ResidentRecord> resolveExistingResident(List<ResidentHostKey> hostKeys) {
        Map<UUID, ResidentRecord> matchesById = new LinkedHashMap<>();
        for (ResidentHostKey hostKey : hostKeys) {
            registry.find(hostKey).ifPresent(record -> matchesById.put(record.residentId(), record));
        }

        if (matchesById.size() > 1) {
            throw new IllegalStateException("Host keys resolve to multiple CIVITAS residents: "
                    + matchesById.keySet());
        }
        return matchesById.values().stream().findFirst();
    }
}

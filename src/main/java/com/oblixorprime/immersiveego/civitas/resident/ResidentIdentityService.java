package com.oblixorprime.immersiveego.civitas.resident;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public final class ResidentIdentityService {
    private final ResidentStore store;
    private final ResidentHostAdapterRegistry adapters;

    public ResidentIdentityService(ResidentStore store, ResidentHostAdapterRegistry adapters) {
        this.store = store;
        this.adapters = adapters;
    }

    public Optional<ResidentRecord> find(Object host) {
        List<ResidentHostKey> hostKeys = requireHostKeys(host);
        return hostKeys.stream()
                .map(store::find)
                .flatMap(Optional::stream)
                .findFirst();
    }

    public ResidentRecord getOrCreate(Object host, long gameTime, Supplier<UUID> residentIdSupplier) {
        return getOrCreate(requireHostKeys(host), gameTime, residentIdSupplier);
    }

    public ResidentRecord getOrCreateAll(
            Collection<?> hosts,
            long gameTime,
            Supplier<UUID> residentIdSupplier) {
        if (hosts == null || hosts.isEmpty()) {
            throw new IllegalArgumentException("At least one resident host is required");
        }

        List<ResidentHostKey> hostKeys = new ArrayList<>();
        for (Object host : hosts) {
            hostKeys.addAll(requireHostKeys(host));
        }
        return getOrCreateKeys(hostKeys, gameTime, residentIdSupplier);
    }

    public ResidentRecord getOrCreateKeys(
            Collection<ResidentHostKey> hostKeys,
            long gameTime,
            Supplier<UUID> residentIdSupplier) {
        Objects.requireNonNull(hostKeys, "hostKeys");
        if (hostKeys.isEmpty()) {
            throw new IllegalArgumentException("At least one resident host key is required");
        }

        return getOrCreate(
                hostKeys.stream()
                        .map(hostKey -> Objects.requireNonNull(hostKey, "hostKey"))
                        .toList(),
                gameTime,
                residentIdSupplier);
    }

    public ResidentHostKey requireHostKey(Object host, CivitasAuthority authority) {
        Objects.requireNonNull(authority, "authority");
        return requireHostKeys(host).stream()
                .filter(hostKey -> hostKey.authority() == authority)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Host " + host.getClass().getName() + " did not identify as "
                                + authority.serializedId()));
    }

    private ResidentRecord getOrCreate(
            List<ResidentHostKey> hostKeys,
            long gameTime,
            Supplier<UUID> residentIdSupplier) {
        ResidentRecord resident = resolveExistingResident(hostKeys)
                .orElseGet(() -> store.getOrCreate(hostKeys.getFirst(), gameTime, residentIdSupplier));

        for (ResidentHostKey hostKey : hostKeys) {
            if (store.find(hostKey).isEmpty()) {
                resident = store.linkHost(resident.residentId(), hostKey, gameTime);
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
            store.find(hostKey).ifPresent(record -> matchesById.put(record.residentId(), record));
        }

        if (matchesById.size() > 1) {
            throw new IllegalStateException("Host keys resolve to multiple CIVITAS residents: "
                    + matchesById.keySet());
        }
        return matchesById.values().stream().findFirst();
    }
}

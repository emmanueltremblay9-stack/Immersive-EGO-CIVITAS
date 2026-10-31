package com.oblixorprime.immersiveego.civitas.resident;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
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
        requireDistinctAuthorities(hostKeys);
        return resolveExistingResident(hostKeys);
    }

    public ResidentRecord getOrCreate(Object host, long gameTime, Supplier<UUID> residentIdSupplier) {
        List<ResidentHostKey> hostKeys = requireHostKeys(host);
        requireDistinctAuthorities(hostKeys);
        return getOrCreate(hostKeys, gameTime, residentIdSupplier);
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

        List<ResidentHostKey> resolvedHostKeys = hostKeys.stream()
                .map(hostKey -> Objects.requireNonNull(hostKey, "hostKey"))
                .toList();
        requireDistinctAuthorities(resolvedHostKeys);

        return getOrCreate(resolvedHostKeys, gameTime, residentIdSupplier);
    }

    public ResidentHostKey requireHostKey(Object host, CivitasAuthority authority) {
        Objects.requireNonNull(authority, "authority");
        if (host == null) {
            throw new IllegalArgumentException(authorityDisplayName(authority) + " host is required");
        }
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
        if (host == null) {
            throw new IllegalArgumentException("Resident host is required");
        }
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

    private static void requireDistinctAuthorities(List<ResidentHostKey> hostKeys) {
        EnumMap<CivitasAuthority, ResidentHostKey> hostsByAuthority = new EnumMap<>(CivitasAuthority.class);
        for (ResidentHostKey hostKey : hostKeys) {
            ResidentHostKey previousHost = hostsByAuthority.putIfAbsent(hostKey.authority(), hostKey);
            if (previousHost != null) {
                throw new IllegalArgumentException(
                        "Duplicate CIVITAS host authority '" + hostKey.authority().serializedId()
                                + "' in one identity request: " + previousHost.hostId()
                                + " and " + hostKey.hostId());
            }
        }
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

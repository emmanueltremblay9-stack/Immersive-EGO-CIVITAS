package com.oblixorprime.immersiveego.civitas.resident;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record ResidentRecord(
        UUID residentId,
        long createdGameTime,
        long updatedGameTime,
        Map<CivitasAuthority, ResidentHostKey> hosts) {
    public ResidentRecord {
        if (residentId == null) {
            throw new IllegalArgumentException("residentId is required");
        }
        if (createdGameTime < 0L) {
            throw new IllegalArgumentException("createdGameTime must be non-negative");
        }
        if (updatedGameTime < createdGameTime) {
            throw new IllegalArgumentException("updatedGameTime cannot predate createdGameTime");
        }
        EnumMap<CivitasAuthority, ResidentHostKey> copiedHosts = new EnumMap<>(CivitasAuthority.class);
        if (hosts != null) {
            for (Map.Entry<CivitasAuthority, ResidentHostKey> entry : hosts.entrySet()) {
                if (entry.getKey() != entry.getValue().authority()) {
                    throw new IllegalArgumentException("host key authority mismatch for " + entry.getValue());
                }
                copiedHosts.put(entry.getKey(), entry.getValue());
            }
        }
        hosts = Map.copyOf(copiedHosts);
    }

    public static ResidentRecord create(UUID residentId, ResidentHostKey hostKey, long gameTime) {
        return new ResidentRecord(
                residentId,
                gameTime,
                gameTime,
                Map.of(hostKey.authority(), hostKey));
    }

    public Optional<ResidentHostKey> host(CivitasAuthority authority) {
        return Optional.ofNullable(hosts.get(authority));
    }

    public Collection<ResidentHostKey> hostKeys() {
        return hosts.values();
    }

    ResidentRecord withHost(ResidentHostKey hostKey, long gameTime) {
        EnumMap<CivitasAuthority, ResidentHostKey> nextHosts = new EnumMap<>(CivitasAuthority.class);
        nextHosts.putAll(hosts);
        nextHosts.put(hostKey.authority(), hostKey);
        return new ResidentRecord(
                residentId,
                createdGameTime,
                Math.max(gameTime, updatedGameTime),
                nextHosts);
    }

    static List<ResidentRecord> sorted(Collection<ResidentRecord> records) {
        return records.stream()
                .sorted((left, right) -> left.residentId().compareTo(right.residentId()))
                .toList();
    }
}

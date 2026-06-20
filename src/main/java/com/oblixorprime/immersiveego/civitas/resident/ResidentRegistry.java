package com.oblixorprime.immersiveego.civitas.resident;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public final class ResidentRegistry implements ResidentStore {
    private final Map<UUID, ResidentRecord> recordsById = new LinkedHashMap<>();
    private final Map<ResidentHostKey, UUID> residentIdsByHost = new LinkedHashMap<>();

    public Collection<ResidentRecord> records() {
        return ResidentRecord.sorted(recordsById.values());
    }

    public Optional<ResidentRecord> find(UUID residentId) {
        return Optional.ofNullable(recordsById.get(residentId));
    }

    @Override
    public Optional<ResidentRecord> find(ResidentHostKey hostKey) {
        return Optional.ofNullable(residentIdsByHost.get(hostKey))
                .flatMap(this::find);
    }

    @Override
    public ResidentRecord getOrCreate(ResidentHostKey hostKey, long gameTime, Supplier<UUID> residentIdSupplier) {
        return find(hostKey)
                .orElseGet(() -> register(ResidentRecord.create(residentIdSupplier.get(), hostKey, gameTime)));
    }

    @Override
    public ResidentRecord linkHost(UUID residentId, ResidentHostKey hostKey, long gameTime) {
        ResidentRecord existing = recordsById.get(residentId);
        if (existing == null) {
            throw new IllegalArgumentException("Unknown resident id " + residentId);
        }

        UUID currentOwner = residentIdsByHost.get(hostKey);
        if (currentOwner != null && !currentOwner.equals(residentId)) {
            throw new IllegalStateException(
                    "Host " + hostKey + " is already linked to resident " + currentOwner);
        }

        ResidentRecord updated = existing.withHost(hostKey, gameTime);
        recordsById.put(residentId, updated);
        existing.host(hostKey.authority())
                .filter(previousHost -> !previousHost.equals(hostKey))
                .ifPresent(residentIdsByHost::remove);
        residentIdsByHost.put(hostKey, residentId);
        return updated;
    }

    ResidentRecord register(ResidentRecord record) {
        if (recordsById.containsKey(record.residentId())) {
            throw new IllegalStateException("Resident id already exists: " + record.residentId());
        }
        for (ResidentHostKey hostKey : record.hostKeys()) {
            UUID currentOwner = residentIdsByHost.get(hostKey);
            if (currentOwner != null && !currentOwner.equals(record.residentId())) {
                throw new IllegalStateException(
                        "Host " + hostKey + " is already linked to resident " + currentOwner);
            }
        }

        recordsById.put(record.residentId(), record);
        for (ResidentHostKey hostKey : record.hostKeys()) {
            residentIdsByHost.put(hostKey, record.residentId());
        }
        return record;
    }
}

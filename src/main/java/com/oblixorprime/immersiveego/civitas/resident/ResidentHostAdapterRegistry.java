package com.oblixorprime.immersiveego.civitas.resident;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ResidentHostAdapterRegistry {
    private final Map<CivitasAuthority, ResidentHostAdapter<?>> adaptersByAuthority =
            new EnumMap<>(CivitasAuthority.class);

    public void register(ResidentHostAdapter<?> adapter) {
        Objects.requireNonNull(adapter, "adapter");
        Objects.requireNonNull(adapter.authority(), "adapter authority");
        Objects.requireNonNull(adapter.hostType(), "adapter host type");

        ResidentHostAdapter<?> previous = adaptersByAuthority.putIfAbsent(adapter.authority(), adapter);
        if (previous != null) {
            throw new IllegalStateException("Adapter already registered for " + adapter.authority());
        }
    }

    public Optional<ResidentHostAdapter<?>> adapter(CivitasAuthority authority) {
        return Optional.ofNullable(adaptersByAuthority.get(authority));
    }

    public Collection<ResidentHostAdapter<?>> adapters() {
        return List.copyOf(adaptersByAuthority.values());
    }

    public List<ResidentHostKey> identify(Object host) {
        Objects.requireNonNull(host, "host");
        List<ResidentHostKey> hostKeys = new ArrayList<>();
        for (ResidentHostAdapter<?> adapter : adaptersByAuthority.values()) {
            adapter.identifyIfSupported(host).ifPresent(hostKeys::add);
        }
        return List.copyOf(hostKeys);
    }
}

package com.oblixorprime.immersiveego.civitas.resident.upstream;

import com.oblixorprime.immersiveego.civitas.resident.ResidentHostAdapterRegistry;

public final class UpstreamResidentHostAdapters {
    private UpstreamResidentHostAdapters() {
    }

    public static ResidentHostAdapterRegistry createRegistry() {
        ResidentHostAdapterRegistry registry = new ResidentHostAdapterRegistry();
        registry.register(new McaVillagerResidentHostAdapter());
        registry.register(new MineColoniesCitizenResidentHostAdapter());
        return registry;
    }
}

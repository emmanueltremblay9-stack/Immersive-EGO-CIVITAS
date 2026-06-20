package com.oblixorprime.immersiveego.civitas.resident;

import java.util.UUID;
import java.util.function.Supplier;

public interface ResidentStore extends ResidentDirectory {
    ResidentRecord getOrCreate(
            ResidentHostKey hostKey,
            long gameTime,
            Supplier<UUID> residentIdSupplier);

    ResidentRecord linkHost(UUID residentId, ResidentHostKey hostKey, long gameTime);
}

package com.oblixorprime.immersiveego.civitas.resident;

import java.util.Optional;

public interface ResidentDirectory {
    Optional<ResidentRecord> find(ResidentHostKey hostKey);
}

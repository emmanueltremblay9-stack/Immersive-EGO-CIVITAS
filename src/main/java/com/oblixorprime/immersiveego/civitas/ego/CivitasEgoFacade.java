package com.oblixorprime.immersiveego.civitas.ego;

import java.util.Optional;

public interface CivitasEgoFacade {
    CivitasEgoApiStatus apiStatus();

    Optional<CivitasEgoSnapshot> readSnapshot(Object upstreamEgoState);
}

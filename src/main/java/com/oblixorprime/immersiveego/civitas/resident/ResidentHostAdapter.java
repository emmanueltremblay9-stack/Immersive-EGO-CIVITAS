package com.oblixorprime.immersiveego.civitas.resident;

import java.util.Optional;

public interface ResidentHostAdapter<T> {
    CivitasAuthority authority();

    Class<T> hostType();

    Optional<ResidentHostKey> identify(T host);

    default Optional<ResidentHostKey> identifyIfSupported(Object host) {
        if (!hostType().isInstance(host)) {
            return Optional.empty();
        }
        return identify(hostType().cast(host));
    }
}

package com.oblixorprime.immersiveego.civitas.ego;

import java.util.Objects;
import java.util.Optional;

public final class ReflectiveCivitasEgoFacade implements CivitasEgoFacade {
    private final ClassLoader classLoader;

    public ReflectiveCivitasEgoFacade(ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader);
    }

    public static ReflectiveCivitasEgoFacade installedRuntime() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return new ReflectiveCivitasEgoFacade(contextClassLoader);
        }
        return new ReflectiveCivitasEgoFacade(ReflectiveCivitasEgoFacade.class.getClassLoader());
    }

    @Override
    public CivitasEgoApiStatus apiStatus() {
        return CivitasEgoApiContract.inspect(classLoader);
    }

    @Override
    public Optional<CivitasEgoSnapshot> readSnapshot(Object upstreamEgoState) {
        return CivitasEgoStateReader.readSnapshot(upstreamEgoState);
    }
}

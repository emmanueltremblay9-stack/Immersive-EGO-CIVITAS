package com.oblixorprime.immersiveego.civitas.resident;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ResidentIdentityServiceTest {
    private final ResidentHostAdapterRegistry adapters = new ResidentHostAdapterRegistry();
    private final ResidentIdentityService identities;

    ResidentIdentityServiceTest() {
        adapters.register(new FakeMcaAdapter());
        adapters.register(new FakeMineColoniesAdapter());
        identities = new ResidentIdentityService(new ResidentRegistry(), adapters);
    }

    @Test
    void findRejectsNullHost() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> identities.find(null));

        assertEquals("Resident host is required", exception.getMessage());
    }

    @Test
    void getOrCreateRejectsNullHost() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> identities.getOrCreate(null, 10L, UUID::randomUUID));

        assertEquals("Resident host is required", exception.getMessage());
    }

    @Test
    void getOrCreateAllRejectsNullHost() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> identities.getOrCreateAll(Arrays.asList(new FakeMineColoniesHost(17, 42), null), 10L, UUID::randomUUID));

        assertEquals("Resident host is required", exception.getMessage());
    }

    @Test
    void requireHostKeyRejectsNullHost() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> identities.requireHostKey(null, CivitasAuthority.MCA_REBORN));

        assertEquals("MCA Reborn host is required", exception.getMessage());
    }

    @Test
    void getOrCreateKeysRejectsDuplicateAuthorities() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> identities.getOrCreateKeys(
                        List.of(
                                new ResidentHostKey(CivitasAuthority.MINECOLONIES, "colony:17/citizen:42"),
                                new ResidentHostKey(CivitasAuthority.MINECOLONIES, "colony:17/citizen:43"),
                                new ResidentHostKey(CivitasAuthority.MCA_REBORN, "villager_entity:mca-1")),
                        10L,
                        UUID::randomUUID));

        assertEquals("Duplicate CIVITAS host authority 'minecolonies' in one identity request: colony:17/citizen:42 and colony:17/citizen:43", exception.getMessage());
    }

    private record FakeMcaHost(String id) {
    }

    private record FakeMineColoniesHost(int colonyId, int citizenId) {
    }

    private static final class FakeMcaAdapter implements ResidentHostAdapter<FakeMcaHost> {
        @Override
        public CivitasAuthority authority() {
            return CivitasAuthority.MCA_REBORN;
        }

        @Override
        public Class<FakeMcaHost> hostType() {
            return FakeMcaHost.class;
        }

        @Override
        public Optional<ResidentHostKey> identify(FakeMcaHost host) {
            return Optional.of(new ResidentHostKey(authority(), "villager_entity:" + host.id()));
        }
    }

    private static final class FakeMineColoniesAdapter
            implements ResidentHostAdapter<FakeMineColoniesHost> {
        @Override
        public CivitasAuthority authority() {
            return CivitasAuthority.MINECOLONIES;
        }

        @Override
        public Class<FakeMineColoniesHost> hostType() {
            return FakeMineColoniesHost.class;
        }

        @Override
        public Optional<ResidentHostKey> identify(FakeMineColoniesHost host) {
            return Optional.of(new ResidentHostKey(
                    authority(),
                    "colony:" + host.colonyId() + "/citizen:" + host.citizenId()));
        }
    }
}

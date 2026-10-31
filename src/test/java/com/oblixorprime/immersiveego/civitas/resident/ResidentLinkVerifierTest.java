package com.oblixorprime.immersiveego.civitas.resident;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ResidentLinkVerifierTest {
    private final ResidentHostAdapterRegistry adapters = new ResidentHostAdapterRegistry();
    private final ResidentRegistry residents = new ResidentRegistry();
    private final ResidentLinkVerifier verifier;

    ResidentLinkVerifierTest() {
        adapters.register(new FakeMcaAdapter());
        adapters.register(new FakeMineColoniesAdapter());
        verifier = new ResidentLinkVerifier(adapters);
    }

    @Test
    void verifiesMcaAndMineColoniesHostsResolveToOneResident() {
        ResidentHostKey mcaHost = new ResidentHostKey(CivitasAuthority.MCA_REBORN, "villager_entity:mca-1");
        ResidentHostKey mineColoniesHost = new ResidentHostKey(
                CivitasAuthority.MINECOLONIES,
                "colony:17/citizen:42");
        UUID residentId = UUID.fromString("ca017e23-eb04-489e-891d-645fc9826f3d");

        ResidentRecord resident = residents.getOrCreate(mcaHost, 10L, () -> residentId);
        residents.linkHost(resident.residentId(), mineColoniesHost, 11L);

        ResidentLinkVerificationResult result = verifier.verifyMcaMineColoniesLink(
                residents,
                new FakeMcaHost("mca-1"),
                new FakeMineColoniesHost(17, 42));

        assertTrue(result.verified());
        assertEquals(residentId, result.residentId().orElseThrow());
        assertEquals(mcaHost, result.mcaHost().orElseThrow());
        assertEquals(mineColoniesHost, result.mineColoniesHost().orElseThrow());
    }

    @Test
    void rejectsMissingMineColoniesLink() {
        ResidentHostKey mcaHost = new ResidentHostKey(CivitasAuthority.MCA_REBORN, "villager_entity:mca-1");
        residents.getOrCreate(mcaHost, 10L, UUID::randomUUID);

        ResidentLinkVerificationResult result = verifier.verifyMcaMineColoniesLink(
                residents,
                new FakeMcaHost("mca-1"),
                new FakeMineColoniesHost(17, 42));

        assertFalse(result.verified());
        assertTrue(result.message().contains("No CIVITAS resident is linked to MineColonies host"));
    }

    @Test
    void rejectsHostsLinkedToDifferentResidents() {
        ResidentHostKey mcaHost = new ResidentHostKey(CivitasAuthority.MCA_REBORN, "villager_entity:mca-1");
        ResidentHostKey mineColoniesHost = new ResidentHostKey(
                CivitasAuthority.MINECOLONIES,
                "colony:17/citizen:42");
        residents.getOrCreate(mcaHost, 10L, () -> UUID.fromString("af1c11d5-7e20-41b8-9d98-eaa35d8a8afe"));
        residents.getOrCreate(
                mineColoniesHost,
                11L,
                () -> UUID.fromString("8ec43db8-6db6-477b-b285-245fbb5813d6"));

        ResidentLinkVerificationResult result = verifier.verifyMcaMineColoniesLink(
                residents,
                new FakeMcaHost("mca-1"),
                new FakeMineColoniesHost(17, 42));

        assertFalse(result.verified());
        assertTrue(result.message().contains("resolves to resident"));
    }

    @Test
    void rejectsUnsupportedHostPair() {
        ResidentLinkVerificationResult result = verifier.verifyMcaMineColoniesLink(
                residents,
                new Object(),
                new FakeMineColoniesHost(17, 42));

        assertFalse(result.verified());
        assertTrue(result.message().contains("No CIVITAS resident host adapter identified"));
    }

    @Test
    void rejectsNullHostsAsFailedVerificationInsteadOfThrowing() {
        ResidentLinkVerificationResult result = verifier.verifyMcaMineColoniesLink(
                residents,
                null,
                new FakeMineColoniesHost(17, 42));

        assertFalse(result.verified());
        assertTrue(result.message().contains("MCA Reborn host is required"));
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

package com.oblixorprime.immersiveego.civitas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oblixorprime.immersiveego.civitas.resident.CivitasAuthority;
import com.oblixorprime.immersiveego.civitas.resident.ResidentHostAdapter;
import com.oblixorprime.immersiveego.civitas.resident.ResidentHostAdapterRegistry;
import com.oblixorprime.immersiveego.civitas.resident.ResidentHostKey;
import com.oblixorprime.immersiveego.civitas.resident.ResidentIdentityService;
import com.oblixorprime.immersiveego.civitas.resident.ResidentRecord;
import com.oblixorprime.immersiveego.civitas.resident.ResidentRecruitmentResult;
import com.oblixorprime.immersiveego.civitas.resident.ResidentRecruitmentService;
import com.oblixorprime.immersiveego.civitas.resident.ResidentRegistry;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ImmersiveEgoCivitasTest {
    @Test
    void modIdentityMatchesMetadataPin() {
        assertEquals("immersive_ego_civitas", ImmersiveEgoCivitas.MOD_ID);
        assertEquals("Immersive EGO: CIVITAS", ImmersiveEgoCivitas.DISPLAY_NAME);
    }

    @Test
    void runtimeGuardPinsEveryAuditedRuntimeMod() {
        Map<String, CivitasRuntimeDependencyGuard.RuntimeRequirement> requirements =
                CivitasRuntimeDependencyGuard.requiredMods().stream()
                        .collect(Collectors.toMap(
                                CivitasRuntimeDependencyGuard.RuntimeRequirement::modId,
                                requirement -> requirement));

        assertEquals(
                Set.of(
                        "immersive_ego",
                        "minecolonies",
                        "structurize",
                        "blockui",
                        "domum_ornamentum",
                        "multipiston",
                        "mca",
                        "modern_companions",
                        "waystones",
                        "balm"),
                requirements.keySet());
        assertEquals(
                "0.1.0-alpha.35",
                requirements.get("immersive_ego").acceptedVersions().getFirst());
        assertTrue(requirements.get("domum_ornamentum").acceptedVersions().contains("${file.jarVersion}"));
    }

    @Test
    void runtimeGuardAcceptsAuditedVersions() {
        Map<String, String> versions = CivitasRuntimeDependencyGuard.requiredMods().stream()
                .collect(Collectors.toMap(
                        CivitasRuntimeDependencyGuard.RuntimeRequirement::modId,
                        requirement -> requirement.acceptedVersions().getFirst()));

        assertTrue(CivitasRuntimeDependencyGuard.findViolations(
                        modId -> Optional.ofNullable(versions.get(modId)))
                .isEmpty());
    }

    @Test
    void runtimeGuardRejectsMissingAndUnknownVersions() {
        Map<String, String> versions = CivitasRuntimeDependencyGuard.requiredMods().stream()
                .filter(requirement -> !"minecolonies".equals(requirement.modId()))
                .collect(Collectors.toMap(
                        CivitasRuntimeDependencyGuard.RuntimeRequirement::modId,
                        requirement -> requirement.acceptedVersions().getFirst()));
        versions.put("modern_companions", "2.1");

        var violations = CivitasRuntimeDependencyGuard.findViolations(
                modId -> Optional.ofNullable(versions.get(modId)));
        var message = CivitasRuntimeDependencyGuard.errorMessage(violations);

        assertTrue(message.contains("Missing required mod 'MineColonies'"));
        assertTrue(message.contains("Unsupported version for 'Modern Companions'"));
        assertTrue(message.contains("found 2.1"));
    }

    @Test
    void residentRegistryCreatesOneCanonicalRecordPerHost() {
        ResidentRegistry registry = new ResidentRegistry();
        UUID residentId = UUID.fromString("d78c13c7-1b8a-4e68-a73d-32b853e29852");
        ResidentHostKey hostKey = new ResidentHostKey(CivitasAuthority.MCA_REBORN, "villager:12");

        ResidentRecord first = registry.getOrCreate(hostKey, 100L, () -> residentId);
        ResidentRecord second = registry.getOrCreate(hostKey, 200L, UUID::randomUUID);

        assertEquals(residentId, first.residentId());
        assertEquals(first, second);
        assertEquals(first, registry.find(hostKey).orElseThrow());
    }

    @Test
    void residentRegistryRefusesHostReassignment() {
        ResidentRegistry registry = new ResidentRegistry();
        ResidentHostKey firstHost = new ResidentHostKey(CivitasAuthority.MCA_REBORN, "villager:12");
        ResidentHostKey secondHost = new ResidentHostKey(CivitasAuthority.MINECOLONIES, "citizen:44");
        ResidentHostKey conflictingHost = new ResidentHostKey(CivitasAuthority.MCA_REBORN, "villager:13");
        UUID firstId = UUID.fromString("d78c13c7-1b8a-4e68-a73d-32b853e29852");
        UUID secondId = UUID.fromString("a1b7f440-5e9b-46bc-9e5d-b489365dd51c");

        registry.getOrCreate(firstHost, 100L, () -> firstId);
        registry.getOrCreate(secondHost, 101L, () -> secondId);
        assertThrows(IllegalStateException.class, () -> registry.linkHost(secondId, firstHost, 102L));

        ResidentRecord linked = registry.linkHost(firstId, conflictingHost, 103L);
        assertEquals(conflictingHost, linked.host(CivitasAuthority.MCA_REBORN).orElseThrow());
        assertEquals(Optional.empty(), registry.find(firstHost));
    }

    @Test
    void residentIdentityServiceCreatesOneRecordAcrossSupportedHosts() {
        ResidentHostAdapterRegistry adapters = new ResidentHostAdapterRegistry();
        adapters.register(new FakeMcaAdapter());
        adapters.register(new FakeColonyAdapter());
        ResidentIdentityService service = new ResidentIdentityService(new ResidentRegistry(), adapters);
        UUID residentId = UUID.fromString("c1f9cd29-a2b5-4f03-a7d2-d3ac7ef4e8c1");
        FakeDualHost host = new FakeDualHost("villager:12", "citizen:44");

        ResidentRecord first = service.getOrCreate(host, 200L, () -> residentId);
        ResidentRecord second = service.getOrCreate(host, 205L, UUID::randomUUID);

        assertEquals(residentId, first.residentId());
        assertEquals(first.residentId(), second.residentId());
        assertEquals("villager:12", second.host(CivitasAuthority.MCA_REBORN).orElseThrow().hostId());
        assertEquals("citizen:44", second.host(CivitasAuthority.MINECOLONIES).orElseThrow().hostId());
    }

    @Test
    void residentHostAdapterRegistryRejectsDuplicateAuthorityAndUnsupportedHosts() {
        ResidentHostAdapterRegistry adapters = new ResidentHostAdapterRegistry();
        adapters.register(new FakeMcaAdapter());

        assertThrows(IllegalStateException.class, () -> adapters.register(new FakeMcaAdapter()));
        assertThrows(IllegalArgumentException.class, () ->
                new ResidentIdentityService(new ResidentRegistry(), adapters)
                        .getOrCreate(new Object(), 1L, UUID::randomUUID));
    }

    @Test
    void residentRecruitmentLinksMcaVillagerAndMineColoniesCitizen() {
        ResidentHostAdapterRegistry adapters = new ResidentHostAdapterRegistry();
        adapters.register(new FakeRecruitmentMcaAdapter());
        adapters.register(new FakeRecruitmentColonyAdapter());
        ResidentRecruitmentService recruitment = new ResidentRecruitmentService(
                new ResidentIdentityService(new ResidentRegistry(), adapters));
        UUID residentId = UUID.fromString("b7944a6a-09fb-4fef-a378-e1d52f5c9ec1");

        ResidentRecruitmentResult result = recruitment.recruitMcaIntoColony(
                new FakeMcaHost("villager_entity:52cc5adc-a51a-4c98-bc21-3d623f08f8a5"),
                new FakeMineColoniesHost("colony:17/citizen:42"),
                300L,
                () -> residentId);

        assertEquals(residentId, result.resident().residentId());
        assertEquals(result.mcaHost(), result.resident().host(CivitasAuthority.MCA_REBORN).orElseThrow());
        assertEquals(
                result.mineColoniesHost(),
                result.resident().host(CivitasAuthority.MINECOLONIES).orElseThrow());
    }

    @Test
    void residentRecruitmentRejectsHostsAlreadyOwnedByDifferentResidents() {
        ResidentHostAdapterRegistry adapters = new ResidentHostAdapterRegistry();
        adapters.register(new FakeRecruitmentMcaAdapter());
        adapters.register(new FakeRecruitmentColonyAdapter());
        ResidentRegistry registry = new ResidentRegistry();
        ResidentRecruitmentService recruitment = new ResidentRecruitmentService(
                new ResidentIdentityService(registry, adapters));
        FakeMcaHost mcaHost = new FakeMcaHost("villager_entity:52cc5adc-a51a-4c98-bc21-3d623f08f8a5");
        FakeMineColoniesHost colonyHost = new FakeMineColoniesHost("colony:17/citizen:42");

        recruitment.recruitMcaIntoColony(mcaHost, new FakeMineColoniesHost("colony:17/citizen:43"), 300L,
                () -> UUID.fromString("b7944a6a-09fb-4fef-a378-e1d52f5c9ec1"));
        recruitment.recruitMcaIntoColony(new FakeMcaHost("villager_entity:71f7504a-bdab-4e75-888e-ef75f36e7338"),
                colonyHost, 301L, () -> UUID.fromString("48c69dd1-f08b-4bb4-bfc7-3ee0b93991f2"));

        assertThrows(IllegalStateException.class, () ->
                recruitment.recruitMcaIntoColony(mcaHost, colonyHost, 302L, UUID::randomUUID));
    }

    @Test
    void residentRecruitmentRejectsReversedUpstreamHosts() {
        ResidentHostAdapterRegistry adapters = new ResidentHostAdapterRegistry();
        adapters.register(new FakeRecruitmentMcaAdapter());
        adapters.register(new FakeRecruitmentColonyAdapter());
        ResidentRecruitmentService recruitment = new ResidentRecruitmentService(
                new ResidentIdentityService(new ResidentRegistry(), adapters));

        assertThrows(IllegalArgumentException.class, () -> recruitment.recruitMcaIntoColony(
                new FakeMineColoniesHost("colony:17/citizen:42"),
                new FakeMcaHost("villager_entity:52cc5adc-a51a-4c98-bc21-3d623f08f8a5"),
                300L,
                UUID::randomUUID));
    }

    private record FakeDualHost(String mcaId, String colonyId) {
    }

    private record FakeMcaHost(String hostId) {
    }

    private record FakeMineColoniesHost(String hostId) {
    }

    private static final class FakeMcaAdapter implements ResidentHostAdapter<FakeDualHost> {
        @Override
        public CivitasAuthority authority() {
            return CivitasAuthority.MCA_REBORN;
        }

        @Override
        public Class<FakeDualHost> hostType() {
            return FakeDualHost.class;
        }

        @Override
        public Optional<ResidentHostKey> identify(FakeDualHost host) {
            return Optional.of(new ResidentHostKey(authority(), host.mcaId()));
        }
    }

    private static final class FakeColonyAdapter implements ResidentHostAdapter<FakeDualHost> {
        @Override
        public CivitasAuthority authority() {
            return CivitasAuthority.MINECOLONIES;
        }

        @Override
        public Class<FakeDualHost> hostType() {
            return FakeDualHost.class;
        }

        @Override
        public Optional<ResidentHostKey> identify(FakeDualHost host) {
            return Optional.of(new ResidentHostKey(authority(), host.colonyId()));
        }
    }

    private static final class FakeRecruitmentMcaAdapter implements ResidentHostAdapter<FakeMcaHost> {
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
            return Optional.of(new ResidentHostKey(authority(), host.hostId()));
        }
    }

    private static final class FakeRecruitmentColonyAdapter
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
            return Optional.of(new ResidentHostKey(authority(), host.hostId()));
        }
    }

}

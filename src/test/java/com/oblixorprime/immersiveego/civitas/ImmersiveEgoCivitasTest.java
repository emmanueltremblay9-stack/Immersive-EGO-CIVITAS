package com.oblixorprime.immersiveego.civitas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oblixorprime.immersiveego.civitas.resident.CivitasAuthority;
import com.oblixorprime.immersiveego.civitas.resident.ResidentHostKey;
import com.oblixorprime.immersiveego.civitas.resident.ResidentRecord;
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
                "0.1.0-alpha.27",
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

}

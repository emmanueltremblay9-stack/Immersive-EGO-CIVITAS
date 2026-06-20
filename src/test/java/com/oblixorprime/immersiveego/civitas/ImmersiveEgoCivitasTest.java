package com.oblixorprime.immersiveego.civitas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
}

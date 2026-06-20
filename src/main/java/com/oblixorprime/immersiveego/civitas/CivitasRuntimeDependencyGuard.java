package com.oblixorprime.immersiveego.civitas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;

public final class CivitasRuntimeDependencyGuard {
    private static final String AUDIT_SOURCE = "docs/ARTIFACT_AUDIT.md";

    private static final List<RuntimeRequirement> REQUIRED_MODS = List.of(
            exact("immersive_ego", "Immersive EGO", "0.1.0-alpha.29"),
            exact("minecolonies", "MineColonies", "1.1.1319-1.21.1"),
            exact("structurize", "Structurize", "1.0.810-1.21.1-snapshot"),
            exact("blockui", "BlockUI", "1.0.199-1.21.1-snapshot"),
            new RuntimeRequirement(
                    "domum_ornamentum",
                    "Domum Ornamentum",
                    List.of("${file.jarVersion}", "1.0.223-snapshot", "1.0.223-1.21.1-snapshot"),
                    AUDIT_SOURCE),
            exact("multipiston", "Multi-Piston", "1.2.51-1.21.1-snapshot"),
            exact("mca", "MCA Reborn", "7.7.11+1.21.1"),
            exact("modern_companions", "Modern Companions", "2.0"),
            exact("waystones", "Waystones", "21.1.29"),
            exact("balm", "Balm", "21.0.56"));

    private CivitasRuntimeDependencyGuard() {
    }

    public static List<RuntimeRequirement> requiredMods() {
        return REQUIRED_MODS;
    }

    public static void verifyPinnedRuntime(Logger logger) {
        List<String> violations = findViolations(CivitasRuntimeDependencyGuard::installedModVersion);
        if (!violations.isEmpty()) {
            throw new IllegalStateException(errorMessage(violations));
        }

        logger.info(
                "{} pinned runtime dependency check passed: {}",
                ImmersiveEgoCivitas.MOD_ID,
                describePinnedRuntime());
    }

    static List<String> findViolations(VersionLookup versionLookup) {
        List<String> violations = new ArrayList<>();
        for (RuntimeRequirement requirement : REQUIRED_MODS) {
            Optional<String> installedVersion = versionLookup.findVersion(requirement.modId());
            if (installedVersion.isEmpty()) {
                violations.add("Missing required mod '" + requirement.displayName() + "' ("
                        + requirement.modId() + "); expected audited version "
                        + requirement.expectedVersions() + " from " + requirement.auditSource() + ".");
                continue;
            }

            String version = installedVersion.get();
            if (!requirement.accepts(version)) {
                violations.add("Unsupported version for '" + requirement.displayName() + "' ("
                        + requirement.modId() + "): found " + version + ", expected audited version "
                        + requirement.expectedVersions() + " from " + requirement.auditSource() + ".");
            }
        }
        return violations;
    }

    static String errorMessage(List<String> violations) {
        return "Immersive EGO: CIVITAS pinned runtime dependency check failed:\n - "
                + String.join("\n - ", violations);
    }

    private static RuntimeRequirement exact(String modId, String displayName, String version) {
        return new RuntimeRequirement(modId, displayName, List.of(version), AUDIT_SOURCE);
    }

    private static Optional<String> installedModVersion(String modId) {
        return ModList.get()
                .getModContainerById(modId)
                .map(container -> container.getModInfo().getVersion().toString());
    }

    private static String describePinnedRuntime() {
        return REQUIRED_MODS.stream()
                .map(requirement -> requirement.modId() + "=" + requirement.expectedVersions())
                .toList()
                .toString();
    }

    @FunctionalInterface
    interface VersionLookup {
        Optional<String> findVersion(String modId);
    }

    public record RuntimeRequirement(
            String modId,
            String displayName,
            List<String> acceptedVersions,
            String auditSource) {
        public RuntimeRequirement {
            acceptedVersions = List.copyOf(acceptedVersions);
        }

        boolean accepts(String version) {
            return acceptedVersions.contains(version);
        }

        String expectedVersions() {
            return String.join(" or ", acceptedVersions);
        }
    }
}

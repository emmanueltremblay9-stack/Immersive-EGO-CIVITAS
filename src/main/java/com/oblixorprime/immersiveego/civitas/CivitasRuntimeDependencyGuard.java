package com.oblixorprime.immersiveego.civitas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;

public final class CivitasRuntimeDependencyGuard {
    private static final String AUDIT_SOURCE = "docs/ARTIFACT_AUDIT.md";
    private static final String ANY_VERSION = "*";

    private static final List<RuntimeRequirement> REQUIRED_MODS = List.of(
            any("immersive_ego", "Immersive EGO"),
            any("minecolonies", "MineColonies"),
            any("structurize", "Structurize"),
            any("blockui", "BlockUI"),
            any("domum_ornamentum", "Domum Ornamentum"),
            any("multipiston", "Multi-Piston"),
            any("mca", "MCA Reborn"),
            any("modern_companions", "Modern Companions"),
            any("waystones", "Waystones"),
            any("balm", "Balm"));

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
                        + requirement.modId() + "); expected supported version "
                        + requirement.expectedVersions() + " from " + requirement.auditSource() + ".");
                continue;
            }

            String version = installedVersion.get();
            if (!requirement.accepts(version)) {
                violations.add("Unsupported version for '" + requirement.displayName() + "' ("
                        + requirement.modId() + "): found " + version + ", expected supported version "
                        + requirement.expectedVersions() + " from " + requirement.auditSource() + ".");
            }
        }
        return violations;
    }

    static String errorMessage(List<String> violations) {
        return "Immersive EGO: CIVITAS pinned runtime dependency check failed:\n - "
                + String.join("\n - ", violations);
    }

    private static RuntimeRequirement any(String modId, String displayName) {
        return new RuntimeRequirement(modId, displayName, List.of(ANY_VERSION), AUDIT_SOURCE);
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
            if (acceptedVersions.contains(ANY_VERSION)) {
                return true;
            }

            for (String acceptedVersion : acceptedVersions) {
                if (isPatchFamily(acceptedVersion)) {
                    String prefix = acceptedVersion.substring(0, acceptedVersion.length() - 1);
                    if (version.startsWith(prefix) && isNumericPatch(version.substring(prefix.length()))) {
                        return true;
                    }
                    continue;
                }

                if (acceptedVersion.equals(version)) {
                    return true;
                }
            }

            return false;
        }

        String expectedVersions() {
            if (acceptedVersions.contains(ANY_VERSION)) {
                return "any installed version";
            }
            return String.join(" or ", acceptedVersions);
        }

        private static boolean isPatchFamily(String acceptedVersion) {
            return acceptedVersion.endsWith(".x");
        }

        private static boolean isNumericPatch(String patch) {
            if (patch.isBlank()) {
                return false;
            }

            int prefixLength = 0;
            while (prefixLength < patch.length() && Character.isDigit(patch.charAt(prefixLength))) {
                prefixLength++;
            }
            return prefixLength > 0;
        }
    }
}

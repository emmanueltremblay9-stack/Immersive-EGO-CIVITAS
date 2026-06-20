# Upstream Immersive EGO API Requirements

Local source audited: `C:\Users\Emmanuel Tremblay\AI Depot\Codex Documents\Immersive EGO`

Current local state:

- Remote: `https://github.com/emmanueltremblay9-stack/Immersive-EGO.git`
- Branch: `main`
- Commit: `789238c475ecabc19808b9ac7d99df7f457670b8`
- Prism LAB prerequisite: `immersive_ego-0.1.0-alpha.35.jar`
- SHA-256: `af1f7a6f0662d080d33cfbff13de67c813127c7fc81c709bbdcf54567a0ce3a9`
- License: MIT
- Minecraft: `1.21.1`
- NeoForge: `21.1.233`
- Source-to-binary proof: local binary parity only. The sibling build artifact
  hash matches the installed Prism LAB jar, but the sibling repository has
  uncommitted changes, so immutable source mapping is blocked until that state
  is committed and pushed or alpha.27 is restored.

## Required neutral CIVITAS facade capabilities

- Resolve or register a supported NPC subject.
- Read bounded hydration, nutrition, sleep, fatigue, stress, origin, and
  readiness snapshots.
- Query ability to work, fight, travel, and recover.
- Submit civic stimuli without importing GPL CIVITAS implementation into the MIT
  Immersive EGO repository.
- Advance unloaded state if the upstream API supports it.

## Current status

The presence of these capabilities has not been proven. P2 must inspect the
actual Immersive EGO public API and either bind to documented interfaces or
record precise upstream API requirements.

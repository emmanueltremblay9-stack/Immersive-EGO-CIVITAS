# Upstream Immersive EGO API Requirements

Local source audited: `C:\Users\Emmanuel Tremblay\AI Depot\Codex Documents\Immersive EGO`

Current local state:

- Remote: `https://github.com/emmanueltremblay9-stack/Immersive-EGO.git`
- Branch: `main`
- Commit: `789238c475ecabc19808b9ac7d99df7f457670b8`
- Prism LAB prerequisite: `immersive_ego-0.1.0-alpha.27.jar`
- SHA-256: `f76dd02414a960a23bb627d59307b9e54f05da1f725adebd2ae3e0ebd8c11329`
- License: MIT
- Minecraft: `1.21.1`
- NeoForge: `21.1.233`
- Source-to-binary proof: passed. A fresh `.\gradlew.bat --no-daemon clean build` in the sibling repository reproduced the installed Prism LAB jar hash, and `.\gradlew.bat --no-daemon runGameTestServer` passed with 3 required tests.

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

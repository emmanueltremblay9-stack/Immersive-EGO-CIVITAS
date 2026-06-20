# Upstream Immersive EGO API Requirements

Local source audited: `C:\Users\Emmanuel Tremblay\AI Depot\Codex Documents\Immersive EGO`

Current local state:

- Remote: `https://github.com/emmanueltremblay9-stack/Immersive-EGO.git`
- Branch: `main`
- Commit: `6a2f87ce56a35e78f3231daf3b03c43c9b2ca60a`
- Version: `0.1.0-alpha.17` in the sibling source checkout; Prism LAB smoke used `immersive_ego-0.1.0-alpha.17.jar`
- License: MIT
- Minecraft: `1.21.1`
- NeoForge: `21.1.233`
- Source-to-binary proof: pending because the sibling checkout has uncommitted changes.

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

# Immersive EGO: CIVITAS

> **MineColonies supplies the colony. MCA supplies the people. Modern Companions supplies the military. Immersive EGO supplies the body and mind. CIVITAS turns them into one society.**

## Project identity

| Field | Value |
|---|---|
| Mod ID | `immersive_ego_civitas` |
| Base package | `com.oblixorprime.immersiveego.civitas` |
| Minecraft | `1.21.1` |
| Loader | `NeoForge` |
| Java | `21` |
| License | `GPL-3.0-only` |
| Current harness | `0.1.0-alpha.9` |
| Planning baseline | `2026-06-19` |

## Four mandatory pillars

1. **MineColonies** - territory, buildings, housing, jobs, work orders, requests, warehouse, permissions, raids and guards.
2. **MCA Reborn** - human identity, appearance, personality, family, relationships, age and dialogue.
3. **Modern Companions** - combat roles, orders, equipment, XP, traits, morale and bond, adapted into host-neutral services.
4. **Immersive EGO** - physiology, sleep, nutrition, psychology, biome origin and readiness.

## Non-negotiable rules

- Required dependencies remain separately installed JARs.
- Useful GPL code may be copied and generalized only with exact provenance.
- Upstream logos, textures, sounds, skins, models, structures and GUI art are not copied by default.
- No new CIVITAS blocks, items, fluids, entities or Blockbench models are required.
- MCA villagers become colony workers without destructive entity replacement.
- MCA remains family authority; MineColonies remains colony authority; Immersive EGO remains needs authority.
- Every material gameplay rule is configurable.
- Dedicated-server safety, atomic logistics and save migration are release gates.
- The bootstrap fails fast if the installed mandatory runtime mods do not match
  the audited LAB version pins in `docs/ARTIFACT_AUDIT.md`.

## First playable target

A desert MineColonies settlement recruits an MCA villager, assigns family-aware housing and work, consumes Immersive EGO readiness, fulfills a warehouse request, and uses a generalized guard role during drought and raid conditions.

## Imported databases

- Roadmap
- Milestones
- Backlog
- Dependencies
- License Matrix
- Risks
- Acceptance Criteria
- Architecture Decisions
- Configuration Catalog
- Test Cases
- Release Checklist
- Feature Ownership Matrix
- Compatibility Matrix
- Code Adaptation Manifest
- Codex Sessions

## Development

```powershell
.\gradlew.bat --no-daemon clean build
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\validate-provenance.ps1
pwsh -NoProfile -ExecutionPolicy Bypass -File .\install-mod.ps1 -SkipBuild
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\install-runtime-deps.ps1
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-gametest-smoke.ps1
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-prism-client-smoke.ps1 -TimeoutSeconds 240
```

The current harness build produces `immersive_ego_civitas-0.1.0-alpha.9.jar`.
The installer writes `build/install-report.json`; runtime dependency install
proof is written to `build/runtime-deps-report.json`; client smoke proof is
written to `build/client-smoke-report.json`.

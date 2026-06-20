# TASKS

## Current milestone

- [x] M0 / P0 - Governance, legal audit, source pinning

## Active tasks

- [x] CIV-001 - Create GPL repository skeleton
  - Acceptance: LICENSE, AGENTS, TASKS and docs exist.
  - Upstream source/commit: Original CIVITAS repository work.
  - Provenance manifest ID: N/A, no upstream code copied.
  - Tests: `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts/validate-provenance.ps1`
  - Evidence: P0 skeleton docs, manifest, notices, and validation script added.
- [x] CIV-002 - Audit exact versions and NeoForge intersection
  - Acceptance: One reproducible dependency set is documented.
  - Upstream source/commit: MineColonies `35bd7ad7448c562c84d11dc9dff5b067e8f131e5`; MCA Reborn `802ab602a7e2aea6284853722ffde88f23cd6840`; Immersive EGO `789238c475ecabc19808b9ac7d99df7f457670b8`.
  - Provenance manifest ID: N/A, audit only.
  - Tests: `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts/validate-provenance.ps1`
  - Evidence: `docs/DEPENDENCY_AUDIT.md`, `docs/ARTIFACT_AUDIT.md`.
- [ ] CIV-003 - Map release artifacts to immutable source commits
  - Acceptance: Every mandatory jar maps to source.
  - Progress: Structurize, BlockUI, Domum Ornamentum, Multi-Piston, Waystones, and Balm now map to immutable source commits/tags in `docs/ARTIFACT_AUDIT.md`. Immersive EGO `0.1.0-alpha.33` binary parity is proven against the sibling build artifact, but the sibling source checkout is dirty.
  - Blocker: Immersive EGO `0.1.0-alpha.33` must be committed/pushed or restored to the previously mapped alpha.27 source state. Modern Companions CurseForge `2.0` artifact is verified, but no checked public `STRHercules/ModernCompanions` branch declares `2.0`, public branch heads predate the April 10, 2026 CurseForge upload, public tags stop at `v1.2.0`, GitHub/Modrinth public releases stop at `v1.2.0`, and `MajorBonghits/ModernCompanions` returned `404`.
- [x] CIV-008 - Initialize NeoForge 1.21.1 Java 21 project
  - Acceptance: Project compiles with standard runs.
  - Upstream source/commit: Original CIVITAS harness; no upstream implementation source copied.
  - Provenance manifest ID: N/A, original source only.
  - Tests: `.\gradlew.bat --no-daemon clean build`; `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\validate-provenance.ps1`; `pwsh -NoProfile -ExecutionPolicy Bypass -File .\install-mod.ps1 -SkipBuild`; `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\install-runtime-deps.ps1`.
  - Evidence: NeoForge metadata in `build/libs/immersive_ego_civitas-0.1.0-alpha.2.jar`, `build/install-report.json`, `build/runtime-deps-report.json`.
- [x] CIV-009 - Add server GameTest smoke gate
  - Acceptance: `runGameTestServer` starts with the local LAB dependency stack and runs the CIVITAS smoke test.
  - Upstream source/commit: Original CIVITAS test and build tooling; no upstream implementation source copied.
  - Provenance manifest ID: N/A, original source only.
  - Tests: `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-gametest-smoke.ps1`.
  - Evidence: `build\gametest-smoke.log` reported `All 1 required tests passed :)` with CIVITAS `0.1.0-alpha.4`, Immersive EGO `0.1.0-alpha.27`, MineColonies, MCA, Modern Companions, Waystones, Balm, and the pinned runtime guard marker loaded.
- [x] CIV-010 - Run client smoke boot
  - Acceptance: Prism LAB client reaches a responsive Minecraft client window with CIVITAS `0.1.0-alpha.4` installed and no crash markers.
  - Upstream source/commit: Original CIVITAS smoke tooling; no upstream implementation source copied.
  - Provenance manifest ID: N/A, original script only.
  - Tests: `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-prism-client-smoke.ps1 -TimeoutSeconds 240`.
  - Evidence: `build\client-smoke-report.json` reported `result=passed`; Prism launched `1.21.1 TesT LaB`; log markers found `immersive_ego_civitas-0.1.0-alpha.4.jar`, CIVITAS bootstrap, pinned runtime guard, common setup, LWJGL, OpenAL, sound engine, and block atlas; client window title was `Minecraft NeoForge* 1.21.1`; no failure markers or crash reports were created.
- [x] CIV-011 - Add pinned runtime dependency guard
  - Acceptance: CIVITAS fails clearly when a mandatory runtime mod is missing or not one of the audited versions.
  - Upstream source/commit: Original CIVITAS guard; uses NeoForge `ModList` only, no unresolved upstream gameplay APIs.
  - Provenance manifest ID: N/A, original source only.
  - Tests: `.\gradlew.bat --no-daemon clean build`; `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-gametest-smoke.ps1`; `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-prism-client-smoke.ps1 -TimeoutSeconds 240`.
  - Evidence: Server and client smoke reports both found `pinned runtime dependency check passed` with Immersive EGO `0.1.0-alpha.27`, MineColonies `1.1.1319-1.21.1`, Structurize `1.0.810-1.21.1-snapshot`, BlockUI `1.0.199-1.21.1-snapshot`, Domum Ornamentum `1.0.223-snapshot`, Multi-Piston `1.2.51-1.21.1-snapshot`, MCA `7.7.11+1.21.1`, Modern Companions `2.0`, Waystones `21.1.29`, and Balm `21.0.56`.
- [ ] CIV-058 - Recruit MCA villager into MineColonies colony
  - Acceptance: One resident record; MCA identity preserved.
  - Progress: Original CIVITAS canonical resident identity scaffold is complete: one resident per external host key, reverse host index, cross-resident host collision guard, `SavedData` persistence, neutral host-adapter registry/service abstractions, verified reflection-backed MCA/MineColonies resident host adapters, and original MCA-to-MineColonies resident recruitment orchestration that links exact verified host keys into one record. The exact public MineColonies assignment API surface is proven by source/runtime audit and a GameTest contract. Alpha.14 adds a repairable assignment executor that routes through MineColonies assignment modules and rolls back target module changes on downstream assignment failures, including partial home/work mutations that throw. Alpha.17 adds live building-object module discovery and a coordinator that routes discovered home/work modules through the repairable executor; gameplay event wiring and live colony mutation triggers are still pending.
  - Upstream source/commit: Original CIVITAS source only; no upstream implementation source copied.
  - Provenance manifest ID: N/A, original source only.
  - Tests: `.\gradlew.bat --no-daemon test`; `.\gradlew.bat --no-daemon clean build`; `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\validate-provenance.ps1`; `pwsh -NoProfile -ExecutionPolicy Bypass -File .\install-mod.ps1 -SkipBuild`; `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-gametest-smoke.ps1`; `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-prism-client-smoke.ps1 -TimeoutSeconds 240`.
  - Evidence: `docs/MINECOLONIES_ASSIGNMENT_API_AUDIT.md`; `build\gametest-smoke.log` reported `All 5 required tests passed :)`, including the upstream resident API, MineColonies assignment API contract, and locator runtime type contract, with Immersive EGO `0.1.0-alpha.33`; `build\install-report.json` installed `immersive_ego_civitas-0.1.0-alpha.19.jar`; `build\client-smoke-report.json` reported `result=passed` with a responsive `Minecraft NeoForge* 1.21.1` client and no crash markers.

## Blockers

- Immersive EGO `0.1.0-alpha.33` source mapping is local-binary-only because
  the sibling source checkout has uncommitted changes.
- Modern Companions artifact/source mismatch blocks adaptation and implementation-detail compile usage.
- Modern Companions GitHub repository license endpoint returns 404; mod metadata states GPL-3.0-only but repository-level license still needs authoritative confirmation.

## Completed

- CIV-001 - P0 repository skeleton and provenance gate.
- CIV-002 - Exact selected runtime artifacts and NeoForge intersection audit.
- CIV-008 - Initial NeoForge harness, build, and LAB install.
- CIV-009 - Server GameTest smoke gate with local LAB dependency staging.
- CIV-010 - Prism LAB client smoke gate.
- CIV-011 - Pinned runtime dependency guard.

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
  - Upstream source/commit: MineColonies `35bd7ad7448c562c84d11dc9dff5b067e8f131e5`; MCA Reborn `802ab602a7e2aea6284853722ffde88f23cd6840`; Immersive EGO `6a2f87ce56a35e78f3231daf3b03c43c9b2ca60a`.
  - Provenance manifest ID: N/A, audit only.
  - Tests: `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts/validate-provenance.ps1`
  - Evidence: `docs/DEPENDENCY_AUDIT.md`, `docs/ARTIFACT_AUDIT.md`.
- [ ] CIV-003 - Map release artifacts to immutable source commits
  - Acceptance: Every mandatory jar maps to source.
  - Progress: Structurize, BlockUI, Domum Ornamentum, Multi-Piston, Waystones, and Balm now map to immutable source tags/commits in `docs/ARTIFACT_AUDIT.md`.
  - Blocker: Modern Companions CurseForge `2.0` artifact is verified, but no checked public `STRHercules/ModernCompanions` branch declares `2.0` and `MajorBonghits/ModernCompanions` returned `404`. Immersive EGO source-to-binary proof is still pending a clean sibling worktree rebuild.
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
  - Evidence: `build\gametest-smoke.log` reported `All 1 required tests passed :)` with CIVITAS `0.1.0-alpha.3`, Immersive EGO `0.1.0-alpha.17`, MineColonies, MCA, Modern Companions, Waystones, and Balm loaded.
- [x] CIV-010 - Run client smoke boot
  - Acceptance: Prism LAB client reaches a responsive Minecraft client window with CIVITAS `0.1.0-alpha.3` installed and no crash markers.
  - Upstream source/commit: Original CIVITAS smoke tooling; no upstream implementation source copied.
  - Provenance manifest ID: N/A, original script only.
  - Tests: `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-prism-client-smoke.ps1 -TimeoutSeconds 240`.
  - Evidence: `build\client-smoke-report.json` reported `result=passed`; Prism launched `1.21.1 TesT LaB`; log markers found `immersive_ego_civitas-0.1.0-alpha.3.jar`, CIVITAS bootstrap/common setup, LWJGL, OpenAL, sound engine, and block atlas; client window title was `Minecraft NeoForge* 1.21.1`; no failure markers or crash reports were created.

## Blockers

- Modern Companions artifact/source mismatch blocks adaptation and implementation-detail compile usage.
- Modern Companions GitHub repository license endpoint returns 404; mod metadata states GPL-3.0-only but repository-level license still needs authoritative confirmation.
- Immersive EGO LAB prerequisite source-to-binary proof remains pending until the sibling source tree can be rebuilt from a clean state.
- TownTalk remains a scope question: it is not declared by the selected MineColonies artifact metadata or verified release dependency minimums.

## Completed

- CIV-001 - P0 repository skeleton and provenance gate.
- CIV-002 - Exact selected runtime artifacts and NeoForge intersection audit.
- CIV-008 - Initial NeoForge harness, build, and LAB install.
- CIV-009 - Server GameTest smoke gate with local LAB dependency staging.
- CIV-010 - Prism LAB client smoke gate.

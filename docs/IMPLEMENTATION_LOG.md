# Implementation Log

## 2026-06-20

- Continued from the imported planning pack.
- Re-read `AGENTS.md`, `TASKS.md`, `docs/CODEX_HANDOFF.md`, and the master
  prompt.
- Audited the first P0 dependency/source facts from primary sources and local
  repositories.
- Added P0 repository skeleton docs and provenance/asset validation tooling.
- Downloaded selected runtime jars into ignored `build/audit-artifacts/`,
  extracted mod metadata, recorded SHA-256 hashes, and documented source
  mapping blockers in `docs/ARTIFACT_AUDIT.md`.
- Initialized the NeoForge 1.21.1 Java 21 harness for `immersive_ego_civitas`
  and advanced it to version `0.1.0-alpha.3`.
- Added original bootstrap source only; no upstream implementation code or
  assets were copied.
- Added install verification scripts for the CIVITAS jar and audited runtime
  dependency jars.
- Added a minimal original CIVITAS GameTest smoke gate and a local runtime
  staging script for Gradle runs against the verified LAB dependency stack.
- Added a smoke wrapper that fails on Minecraft crash markers because
  ModDevGradle can report success after a mod-loading crash.
- Identified Modern Companions' Waystones event subscriber as a runtime crash
  source when Waystones is absent; pinned Waystones and Balm runtime support
  jars.
- Added `data/immersive_ego_civitas/structure/empty.nbt` and verified
  `runGameTestServer` reports `All 1 required tests passed :)`.
- Added a Prism LAB client smoke wrapper that launches `1.21.1 TesT LaB`
  with quoted Prism CLI arguments, checks timestamped client log markers, and
  cleans up only the launched LAB client process.
- Verified `scripts\run-prism-client-smoke.ps1 -TimeoutSeconds 240` reported
  `result=passed` with CIVITAS `0.1.0-alpha.3`, LWJGL, OpenAL, sound engine,
  block atlas, a responsive `Minecraft NeoForge* 1.21.1` window, and no crash
  reports.
- Advanced CIV-003 by mapping Structurize, BlockUI, Domum Ornamentum,
  Multi-Piston, Waystones, and Balm runtime artifacts to immutable Git tags and
  source commits. Modern Companions `2.0` remains source-blocked.
- Added an original pinned runtime dependency guard for the audited LAB stack,
  advanced CIVITAS to `0.1.0-alpha.4`, and verified both server and Prism
  client smoke gates find `pinned runtime dependency check passed`.
- Updated the current Immersive EGO LAB prerequisite to
  `immersive_ego-0.1.0-alpha.27.jar` with SHA-256
  `f76dd02414a960a23bb627d59307b9e54f05da1f725adebd2ae3e0ebd8c11329`.
- Rebuilt the sibling Immersive EGO source at commit
  `789238c475ecabc19808b9ac7d99df7f457670b8`; `.\gradlew.bat --no-daemon clean build`
  reproduced the installed alpha.27 jar SHA-256, and
  `.\gradlew.bat --no-daemon runGameTestServer` passed with 3 required tests.
- Refreshed Modern Companions provenance from GitHub, CurseForge, and Modrinth:
  the selected `2.0` CurseForge artifact still has no matching public source
  tag/release/branch, while official GitHub and Modrinth releases stop at
  `v1.2.0`.
- Resolved the TownTalk scope question for the current release gate: TownTalk
  `1.2.0` is an optional All Rights Reserved / GitHub `NOASSERTION` speech
  add-on, not declared by selected MineColonies metadata and not one of the
  four required CIVITAS pillars, so it is documented as non-selected.
- No upstream implementation source was copied or adapted.

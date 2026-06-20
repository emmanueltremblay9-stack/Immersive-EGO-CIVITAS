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
- No upstream implementation source was copied or adapted.

# Architecture

This file is the implementation-facing architecture index. The imported planning
pages remain the detailed product source.

## Authority boundaries

- MineColonies owns colony, territory, buildings, requests, warehouse, jobs,
  permissions, and raids.
- MCA Reborn owns identity, personality, mood, relationships, family, age, and
  dialogue.
- Immersive EGO owns physiology, sleep, fatigue, stress, origin, and readiness.
- CIVITAS owns canonical civic resident identity and cross-system orchestration.
- Modern Companions-derived logic may be adapted only after artifact/source and
  lineage verification.

## Required services

- `ResidentRegistry`
- `ResidentHostAdapterRegistry`
- `CivitasEgoFacade`
- `ColonyFacade`
- `McaSocialFacade`
- `OrderCoordinator`
- `TransactionLedger`
- `ReputationService`
- `CrisisEngine`

## Current state

The NeoForge harness exists with original CIVITAS bootstrap code, the pinned
runtime dependency guard, and the first original resident identity persistence
scaffold. `CivitasResidentSavedData` owns canonical resident records and reverse
host indexing. `ResidentHostAdapterRegistry` and `ResidentIdentityService`
provide neutral adapter seams for future MCA, MineColonies, Immersive EGO, and
Modern Companions bindings. No upstream gameplay implementation source or
assets have been copied. P0 provenance and asset gates remain active. CIV-003
still blocks Modern Companions implementation-detail adaptation until an
immutable source commit mapping is proven.

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
runtime dependency guard, and resident identity persistence. `CivitasResidentSavedData`
owns canonical resident records and reverse host indexing. `ResidentHostAdapterRegistry`
and `ResidentIdentityService` provide neutral adapter surfaces, alpha.8 adds
reflection-backed MCA Reborn and MineColonies resident host adapters using
verified public class/method names, and alpha.9 adds original recruitment
orchestration that links one verified MCA host and one verified MineColonies
host into the same canonical resident record. Alpha.10 adds a runtime contract
for the MineColonies assignment-module APIs that CIVITAS must use before
mutating housing or work state. Alpha.14 adds an original repairable assignment
executor that calls assignment modules and rolls back target module changes on
downstream assignment failure. Alpha.17 adds live building-object module
discovery and a coordinator that routes discovered home/work modules through the
repairable executor. Alpha.21 adds a linked-resident assignment trigger that
fails closed unless the MineColonies citizen is already linked to an MCA-backed
CIVITAS resident. Gameplay event wiring and live colony lookup are still
pending. No upstream gameplay implementation source or assets
have been copied. P0 provenance and asset gates remain active. CIV-003 still
blocks Modern Companions implementation-detail adaptation and release packaging
until immutable source commit mappings are proven for Modern Companions `2.0`
and the current Immersive EGO `0.1.0-alpha.35` prerequisite.

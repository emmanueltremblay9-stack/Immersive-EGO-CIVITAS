# Test Plan

## Current gates

- `scripts/validate-provenance.ps1`
- `scripts/stage-local-runtime-deps.ps1`
- `scripts/run-gametest-smoke.ps1`
- `scripts/run-prism-client-smoke.ps1`
- `.\gradlew.bat --no-daemon clean build`
- `.\gradlew.bat --no-daemon runGameTestServer`
- `.\install-mod.ps1 -SkipBuild`
- `scripts/install-runtime-deps.ps1`
- Pure Java unit tests for the pinned runtime dependency guard, original
  resident registry invariants, neutral host-adapter registry/service, and
  reflection-backed MCA/MineColonies resident host-key adapters.
- Pure Java unit tests for MCA-to-colony resident recruitment linking,
  cross-resident conflict rejection, and reversed upstream host rejection.
- Pure Java unit tests for repairable MineColonies assignment execution,
  rejection/no-op handling, rollback, and partial mutation cleanup.
- Pure Java unit tests for MineColonies assignment module discovery,
  ambiguous target rejection, previous-module rollback discovery, and routing
  discovered modules through the repairable executor.
- Pure Java unit tests for linked-resident assignment trigger guards, including
  unlinked MineColonies hosts, missing MCA links, and successful delegation.
- Pure Java unit tests for MineColonies assignment target lookup, including
  live colony/citizen/building resolution, citizen-only lookup, missing citizen
  rejection, missing manager rejection, and cross-colony target building
  rejection.
- GameTests for runtime boot, resident SavedData host lookup, and resident NBT
  round-trip.
- GameTest runtime contracts for installed MCA Reborn, MineColonies resident
  API, MineColonies assignment API class/method surfaces, live MineColonies
  lookup surfaces, persisted recruitment saved-data linking, and
  linked-resident assignment trigger guard behavior.

## Required future gates

- Pure Java tests for codecs, migrations, resident repair, order arbitration,
  transaction state, reputation, economy, crisis, config validation, and
  simulation tiers.
- GameTests for recruitment, MineColonies assignment, warehouse requests,
  rollback, family persistence, guard/patrol, EGO readiness, child restrictions,
  raid shelter, migration, and gameplay-level unknown-version rejection.
- Forbidden asset/hash scan.
- Adapted-source header and manifest scan.

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
- GameTests for runtime boot, resident SavedData host lookup, and resident NBT
  round-trip.
- GameTest runtime contract for installed MCA Reborn and MineColonies resident
  API class/method surfaces.

## Required future gates

- Pure Java tests for codecs, migrations, resident repair, order arbitration,
  transaction state, reputation, economy, crisis, config validation, and
  simulation tiers.
- GameTests for recruitment, MineColonies assignment, warehouse requests,
  rollback, family persistence, guard/patrol, EGO readiness, child restrictions,
  raid shelter, migration, and gameplay-level unknown-version rejection.
- Forbidden asset/hash scan.
- Adapted-source header and manifest scan.

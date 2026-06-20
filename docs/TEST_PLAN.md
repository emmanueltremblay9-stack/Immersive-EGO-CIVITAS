# Test Plan

## Current gates

- `scripts/validate-provenance.ps1`
- `.\gradlew.bat --no-daemon clean build`
- `.\install-mod.ps1 -SkipBuild`
- `scripts/install-runtime-deps.ps1`

## Required future gates

- Dedicated-server smoke boot.
- Client smoke boot.
- Pure Java tests for codecs, migrations, resident repair, order arbitration,
  transaction state, reputation, economy, crisis, config validation, and
  simulation tiers.
- GameTests for recruitment, MineColonies assignment, warehouse requests,
  rollback, family persistence, guard/patrol, EGO readiness, child restrictions,
  raid shelter, migration, and unknown-version rejection.
- Forbidden asset/hash scan.
- Adapted-source header and manifest scan.

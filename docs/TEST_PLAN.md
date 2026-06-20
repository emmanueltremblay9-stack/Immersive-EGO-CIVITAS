# Test Plan

## Current gates

- `scripts/validate-provenance.ps1`

## Required future gates

- Gradle clean build.
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

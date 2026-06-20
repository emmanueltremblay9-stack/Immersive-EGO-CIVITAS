# Configuration and data-driven design

Every material gameplay rule must be configurable.

## Server config families

```text
immersive_civitas-core.toml
immersive_civitas-residents.toml
immersive_civitas-minecolonies.toml
immersive_civitas-mca.toml
immersive_civitas-companions.toml
immersive_civitas-work.toml
immersive_civitas-logistics.toml
immersive_civitas-factions.toml
immersive_civitas-cultures.toml
immersive_civitas-reputation.toml
immersive_civitas-economy.toml
immersive_civitas-contracts.toml
immersive_civitas-family.toml
immersive_civitas-defense.toml
immersive_civitas-governance.toml
immersive_civitas-crises.toml
immersive_civitas-performance.toml
```

## Dynamic TOML profiles

- Factions
- Cultures
- Professions
- Specializations
- Work recipes
- Trade policies
- Contracts
- Dialogue topics
- Laws
- Crisis profiles
- Compatibility maps

## Loader requirements

- schema version;
- comments and bounds;
- enum validation;
- atomic reload;
- backup and migration;
- last-known-good fallback;
- precise error paths;
- presets;
- immutable runtime snapshots;
- no hot-loop reads from raw config values.

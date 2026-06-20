# Compatibility Matrix

Audit date: 2026-06-20

| Component | Minecraft | Loader | Version | Compatibility status |
|---|---|---|---|---|
| CIVITAS | 1.21.1 | NeoForge | 0.1.0-alpha.23 | Harness builds, installs, and passes server/client smoke with pinned runtime guard, resident SavedData GameTests, verified reflection-backed MCA/MineColonies resident host adapter coverage, original resident recruitment orchestration unit coverage, a MineColonies assignment API contract GameTest, repairable assignment executor unit coverage, live assignment module locator/coordinator unit coverage, linked-resident assignment trigger coverage, and operator server command lookup coverage |
| Immersive EGO | 1.21.1 | NeoForge | 0.1.0-alpha.37 | LAB prerequisite loaded in server/client smoke; installed jar hash matches the sibling build artifact, but the sibling checkout has uncommitted changes so immutable source mapping is blocked |
| NeoForge | 1.21.1 | NeoForge | 21.1.233 candidate | Maven metadata verified |
| MineColonies | 1.21.1 | NeoForge/Forge-family release | 1.1.1319 | Stable source tag mapped |
| MCA Reborn | 1.21.1 | NeoForge asset available | 7.7.11 | Source tag mapped |
| Modern Companions | 1.21.1 | NeoForge | CurseForge 2.0 | Artifact verified; source commit blocked |
| Structurize | 1.21.1 | NeoForge | 1.0.810 snapshot | Artifact verified; source tag `v1.21.1-1.0.810-snapshot` mapped |
| BlockUI | 1.21.1 | NeoForge | 1.0.199 snapshot | Artifact verified; source tag `v1.21.1-1.0.199` mapped |
| Domum Ornamentum | 1.21.1 | NeoForge | 1.0.223 snapshot | Artifact verified; source tag `v1.21.1-1.0.223` mapped; jar metadata version placeholder |
| Multi-Piston | 1.21.1 | NeoForge | 1.2.51 snapshot | Artifact verified; source tag `v1.21.1-1.2.51` mapped |
| Waystones | 1.21.1 | NeoForge | 21.1.29 | Artifact verified; source tag `v21.1.29` mapped; runtime-only due license uncertainty |
| Balm | 1.21.1 | NeoForge | 21.0.56 | Artifact verified; source tag `v21.0.56` mapped; required by Waystones |

The first build harness must fail clearly on unknown mandatory-mod versions.

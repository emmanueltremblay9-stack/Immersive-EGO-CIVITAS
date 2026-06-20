# Dependency Audit

Audit date: 2026-06-20

This is the first P0 audit snapshot. It records verified evidence and blocks
implementation where source or artifact mapping is incomplete.

## Selected baseline

| Dependency | Selected / observed version | Source commit or source state | Status |
|---|---|---|---|
| Minecraft | `1.21.1` | Mojang runtime, external | Pinned target |
| Java | `21` | OpenJDK-compatible runtime | Pinned target |
| NeoForge | `21.1.233` observed in Maven metadata | `neoforged/NeoForge`, 1.21.x active | Candidate loader pin |
| Immersive EGO | `0.1.0-alpha.7` local repo | `6a2f87ce56a35e78f3231daf3b03c43c9b2ca60a` | Local prerequisite |
| MineColonies | `1.1.1319` for Minecraft 1.21.1 | `35bd7ad7448c562c84d11dc9dff5b067e8f131e5` | Stable audit target |
| Structurize | `1.0.810-1.21.1-snapshot` or above | Required by MineColonies release | Minimum recorded |
| Multi-Piston | `1.2.51-1.21.1-snapshot` or above | Required by MineColonies release | Minimum recorded |
| BlockUI | `1.0.199-1.21.1-snapshot` or above | Required by MineColonies release | Minimum recorded |
| Domum Ornamentum | `1.0.223-snapshot` or above | Required by MineColonies release | Minimum recorded |
| TownTalk | Exact 1.21.1 build not yet verified | Not listed in MineColonies 1.1.1319 release dependency minimums | Audit gap |
| MCA Reborn | `7.7.11+1.21.1` | tag commit `802ab602a7e2aea6284853722ffde88f23cd6840` | Source/release mapped |
| Modern Companions | CurseForge `2.0`; GitHub release `v1.2.0` | `v1.2.0` tag commit `9ff82224ccc5709b429e9a68ccdbf35345e59c0a` | Blocked mismatch |

## Evidence

- NeoForge Maven metadata returned current `21.1.x` entries ending at `21.1.233`.
- NeoForge repository identifies 1.21.x support as active and license as
  LGPL-2.1.
- MineColonies release `v1.21.1-1.1.1319` is titled "Version 1.1.1319 for
  Minecraft 1.21.1", points at commit `35bd7ad...`, and lists dependency
  minimums for Structurize, Multi-Piston, BlockUI, and Domum Ornamentum.
- MCA Reborn release `7.7.11+1.21.1` has a NeoForge jar asset and tag commit
  `802ab602...`.
- Local Immersive EGO checkout is at commit `6a2f87ce...`, with
  `mod_version=0.1.0-alpha.7`, `mod_license=MIT`, `minecraft_version=1.21.1`,
  and `neo_version=21.1.233`.
- Modern Companions GitHub `v1.2.0` declares `version=1.2.0`,
  `mod_id=modern_companions`, `minecraft_version=1.21.1`, and
  `neo_version=21.1.1`. Its mod metadata declares `license = "GPL-3.0-only"`.
  The GitHub repository license endpoint returns `404`, and the repository
  release line does not resolve the CurseForge `2.0` runtime artifact.

## Blockers

1. Map Modern Companions CurseForge `2.0` to an immutable source commit.
2. Verify Modern Companions root/source license file or another authoritative
   repository-level license record.
3. Audit Human Companions and Basic Weapons lineage before using Modern
   Companions implementation details.
4. Verify exact TownTalk 1.21.1 artifact and source commit if it remains a
   required runtime dependency for the selected MineColonies set.

## Source links

- NeoForge Maven metadata: https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml
- NeoForge repository: https://github.com/neoforged/NeoForge
- MineColonies 1.1.1319 release: https://github.com/ldtteam/minecolonies/releases/tag/v1.21.1-1.1.1319
- MineColonies source branch: https://github.com/ldtteam/minecolonies/tree/version/1.21
- MCA Reborn 7.7.11 release: https://github.com/Luke100000/minecraft-comes-alive/releases/tag/7.7.11%2B1.21.1
- Modern Companions repository: https://github.com/STRHercules/ModernCompanions
- Modern Companions CurseForge: https://www.curseforge.com/minecraft/mc-mods/modern-companions
- Immersive EGO repository: https://github.com/emmanueltremblay9-stack/Immersive-EGO

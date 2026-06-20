# Dependency Audit

Audit date: 2026-06-20

This is the first P0 audit snapshot. It records verified evidence and blocks
implementation where source mapping is incomplete. Artifact hashes and jar
metadata are recorded in `docs/ARTIFACT_AUDIT.md`.

## Selected baseline

| Dependency | Selected / observed version | Source commit or source state | Status |
|---|---|---|---|
| Minecraft | `1.21.1` | Mojang runtime, external | Pinned target |
| Java | `21` | OpenJDK-compatible runtime | Pinned target |
| NeoForge | `21.1.233` observed in Maven metadata | `neoforged/NeoForge`, 1.21.x active | Candidate loader pin |
| Immersive EGO | `0.1.0-alpha.27` LAB jar observed during smoke | `789238c475ecabc19808b9ac7d99df7f457670b8`; clean rebuild reproduced the installed jar SHA-256 | Local prerequisite mapped |
| MineColonies | `1.1.1319` for Minecraft 1.21.1 | `35bd7ad7448c562c84d11dc9dff5b067e8f131e5` | Stable audit target |
| Structurize | `1.0.810-1.21.1-snapshot` | tag `v1.21.1-1.0.810-snapshot` commit `16a05cdb3524fae1662d8bab1f48b1d28f580c7a` | Source/release mapped |
| Multi-Piston | `1.2.51-1.21.1-snapshot` | tag `v1.21.1-1.2.51` commit `b74560984ea1da1906e59dd2f34286d55ee30449` | Source/release mapped |
| BlockUI | `1.0.199-1.21.1-snapshot` | tag `v1.21.1-1.0.199` commit `29c2e55fa09f3d8168dc2677368188d22e2f3688` | Source/release mapped |
| Domum Ornamentum | `1.0.223-snapshot` | tag `v1.21.1-1.0.223` commit `fe16a052f1eca2b8a2cc0a0a0ed3354f404c1d84` | Source/release mapped |
| TownTalk | Not selected | Not declared by selected MineColonies jar metadata or verified release dependency minimums | Scope question |
| MCA Reborn | `7.7.11+1.21.1` | tag commit `802ab602a7e2aea6284853722ffde88f23cd6840` | Source/release mapped |
| Modern Companions | CurseForge `2.0` artifact verified | Public source branches do not declare `2.0`; `v1.2.0` tag commit `9ff82224ccc5709b429e9a68ccdbf35345e59c0a` maps only the older artifact | Source mapping blocked |
| Waystones | `21.1.29` | tag `v21.1.29` peeled commit `75f923f36938515571fd71fbe8c30ff8050df417` | Runtime source mapped; do not adapt without license permission |
| Balm | `21.0.56` | tag `v21.0.56` peeled commit `f9af2e38e3a8788d0bddd51de8234ffeb1218ddf` | Runtime source mapped |

## Evidence

- NeoForge Maven metadata returned current `21.1.x` entries ending at `21.1.233`.
- NeoForge repository identifies 1.21.x support as active and license as
  LGPL-2.1.
- MineColonies release `v1.21.1-1.1.1319` is titled "Version 1.1.1319 for
  Minecraft 1.21.1", points at commit `35bd7ad...`, and lists dependency
  minimums for Structurize, Multi-Piston, BlockUI, and Domum Ornamentum.
- MCA Reborn release `7.7.11+1.21.1` has a NeoForge jar asset and tag commit
  `802ab602...`.
- MCA Reborn GitHub asset digest for `mca-neoforge-7.7.11+1.21.1.jar`
  matches the local SHA-256 in `docs/ARTIFACT_AUDIT.md`.
- Immersive EGO commit `789238c475ecabc19808b9ac7d99df7f457670b8` declares
  `0.1.0-alpha.27`. A fresh sibling `.\gradlew.bat --no-daemon clean build`
  reproduced `build\libs\immersive_ego-0.1.0-alpha.27.jar` with SHA-256
  `f76dd02414a960a23bb627d59307b9e54f05da1f725adebd2ae3e0ebd8c11329`,
  matching the installed Prism LAB prerequisite jar.
- Modern Companions CurseForge file `7902593` downloads as
  `ModernCompanions-1.21.1-2.0-NeoForge.jar` and its metadata declares
  `version = "2.0"`, `license = "GPL-3.0-only"`, required NeoForge
  `21.1.215`, and required Minecraft `1.21.1`.
- CurseForge's Modern Companions source link points to
  `STRHercules/ModernCompanions`, but public branches checked there declare
  versions `0.1.91`, `1.1.5`, `1.2.0`, `1.2.5`, `1.2.12`, or `1.2.37`, not
  `2.0`. GitHub and Modrinth official release APIs list no release beyond
  `v1.2.0`. The jar issue tracker URL points to
  `MajorBonghits/ModernCompanions`, which returned `404` through the GitHub
  API.
- `git ls-remote --heads --tags` resolved matching source tags for
  Structurize `v1.21.1-1.0.810-snapshot`, BlockUI `v1.21.1-1.0.199`, Domum
  Ornamentum `v1.21.1-1.0.223`, Multi-Piston `v1.21.1-1.2.51`, Waystones
  `v21.1.29`, and Balm `v21.0.56`.
- Raw `gradle.properties` reads at those commits confirmed the selected
  Minecraft/version lines for the mapped artifacts, except LDT CI still
  injects release artifact versions while some `gradle.properties` files keep
  local placeholder versions.
- GitHub license detection reports GPL-3.0 for Structurize, BlockUI, Domum
  Ornamentum, and Multi-Piston; Balm is Apache-2.0; Waystones is `NOASSERTION`
  and its jar metadata says `All Rights Reserved`, so treat it as runtime-only.

## Blockers

1. Map Modern Companions CurseForge `2.0` to an immutable source commit.
2. Verify Modern Companions root/source license file or another authoritative
   repository-level license record.
3. Audit Human Companions and Basic Weapons lineage before using Modern
   Companions implementation details.
4. Resolve whether TownTalk remains a required CIVITAS dependency despite not
   being declared by the selected MineColonies runtime artifact.

## Source links

- NeoForge Maven metadata: https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml
- NeoForge repository: https://github.com/neoforged/NeoForge
- MineColonies 1.1.1319 release: https://github.com/ldtteam/minecolonies/releases/tag/v1.21.1-1.1.1319
- MineColonies source branch: https://github.com/ldtteam/minecolonies/tree/version/1.21
- Structurize repository: https://github.com/ldtteam/Structurize
- BlockUI repository: https://github.com/ldtteam/BlockUI
- Domum Ornamentum repository: https://github.com/ldtteam/Domum-Ornamentum
- Multi-Piston source repository: https://github.com/ldtteam/Piston-Unlimited
- MCA Reborn 7.7.11 release: https://github.com/Luke100000/minecraft-comes-alive/releases/tag/7.7.11%2B1.21.1
- Modern Companions repository: https://github.com/STRHercules/ModernCompanions
- Modern Companions CurseForge: https://www.curseforge.com/minecraft/mc-mods/modern-companions/files/7902593
- Immersive EGO repository: https://github.com/emmanueltremblay9-stack/Immersive-EGO
- Waystones repository: https://github.com/TwelveIterationMods/Waystones
- Balm repository: https://github.com/TwelveIterationMods/Balm

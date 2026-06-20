# Artifact Audit

Audit date: 2026-06-20

This file records artifact-level evidence for the first CIVITAS runtime set.
Downloaded jars are kept only in the ignored local cache
`build/audit-artifacts/`; they are not repository artifacts.

## Selected runtime set

| Component | Artifact locator | Size | SHA-256 | Metadata evidence |
|---|---|---:|---|---|
| NeoForge universal | `https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.233/neoforge-21.1.233-universal.jar` | 3542450 | `ff997bfd7db9735755545a0d073bfb56bef55a8c9dd17bc1bba215a5dd55fc0d` | `modId=neoforge`, version `21.1.233`, license `LGPL v2.1` |
| NeoForge installer | `https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.233/neoforge-21.1.233-installer.jar` | 6964847 | `311475c8315ed0be6b5f1dbbf5a377b6c0976457c0bd5aa6d19b0fe25fd77148` | Maven `.sha256` sidecar matched |
| Immersive EGO | LAB prerequisite `immersive_ego-0.1.0-alpha.27.jar` | 290363 | `f76dd02414a960a23bb627d59307b9e54f05da1f725adebd2ae3e0ebd8c11329` | `modId=immersive_ego`, version `0.1.0-alpha.27`, license `MIT`, NeoForge `[21.1.228,)`; sibling source worktree is dirty |
| MineColonies | CurseForge project `245506`, file `8138370`, `minecolonies-1.1.1319-1.21.1.jar` | 77496046 | `ab97c0eec45c3f2539ec31428e3c836bb30ba1c537af0c86f5ab4e38754f6a4d` | `modId=minecolonies`, version `1.1.1319-1.21.1`, license `GPL3`, NeoForge `[21.1.0, )` |
| Structurize | CurseForge project `298744`, file `7643353`, `structurize-1.0.810-1.21.1-snapshot.jar` | 1058690 | `7379ee90fde4abaeda6857d954e7cfa1ddb07a526bd425d3f7f23ae47d81ed14` | `modId=structurize`, version `1.0.810-1.21.1-snapshot`, license `GPL 3.0`, NeoForge `[21.0.143,)` |
| BlockUI | CurseForge project `522992`, file `6367809`, `blockui-1.0.199-1.21.1-snapshot.jar` | 468231 | `238b2e9fda99620318dfa9197754b3f803fff73f7f711f1065ac900d2e4ee9ef` | `modId=blockui`, version `1.0.199-1.21.1-snapshot`, license `GPL3`, NeoForge `[21.1,)` |
| Domum Ornamentum | CurseForge project `527361`, file `7231908`, `domum-ornamentum-1.0.223-snapshot-main.jar` | 1124673 | `e208671d86050bd49e48b9524dfa12f1f642efefc8fa31eff88ef24709d31f83` | `modId=domum_ornamentum`, license `GPL3`, metadata version is unresolved placeholder `${file.jarVersion}` |
| Multi-Piston | CurseForge project `303278`, file `5783614`, `multipiston-1.2.51-1.21.1-snapshot.jar` | 33318 | `d0eafb395fdf1e6962b5cb127f26488124dc7d5008361b164576423820f3782e` | `modId=multipiston`, version `1.2.51-1.21.1-snapshot`, license `GPL3`, NeoForge `[21.0.143,)` |
| MCA Reborn | GitHub release asset `Luke100000/minecraft-comes-alive`, tag `7.7.11+1.21.1`, `mca-neoforge-7.7.11+1.21.1.jar` | 10471867 | `8d569c0ae870e1fe098a7270f240780aa588f328512f64ffa0a6d74a886fc59f` | GitHub asset digest matched; `modId=mca`, version `7.7.11+1.21.1`, license `GPL-3.0` |
| Modern Companions | CurseForge project `1391597`, file `7902593`, `ModernCompanions-1.21.1-2.0-NeoForge.jar` | 870678 | `fb7085db4f1f99f7fcd845470685ff9f86348a03db5727f06f74ce685e4ab312` | `modId=modern_companions`, version `2.0`, license `GPL-3.0-only`, NeoForge `21.1.215` |
| Waystones | CurseForge project `245755`, file `7750240`, `waystones-neoforge-1.21.1-21.1.29.jar` | 876329 | `4ba58cbff6600179addd06f8e27912339ff4aca9624c279caa173aaebc68a3a8` | `modId=waystones`, version `21.1.29`, NeoForge `[21-beta,)`, required by Modern Companions event subscriber at runtime |
| Balm | CurseForge project `531761`, file `7420963`, `balm-neoforge-1.21.1-21.0.56.jar` | 739709 | `670ed64e73cacb646b21eaa6e2d2028f95c498d0cf1ae5cffcf7035d0b2fb32e` | `modId=balm`, version `21.0.56`, NeoForge `[21.0.82-beta,)`, required by Waystones |

## Source mapping

| Component | Source status |
|---|---|
| MineColonies | Release tag `v1.21.1-1.1.1319` maps to commit `35bd7ad7448c562c84d11dc9dff5b067e8f131e5`. |
| MCA Reborn | Release tag `7.7.11+1.21.1` maps to commit `802ab602a7e2aea6284853722ffde88f23cd6840`. |
| Immersive EGO | Local repository HEAD is `6a2f87ce56a35e78f3231daf3b03c43c9b2ca60a`; LAB now uses `0.1.0-alpha.27`, but source-to-binary proof remains conditional because that external worktree has uncommitted changes. |
| NeoForge | Maven artifacts and hashes are pinned; no CIVITAS source is adapted from NeoForge. |
| Structurize | Tag `v1.21.1-1.0.810-snapshot` maps to commit `16a05cdb3524fae1662d8bab1f48b1d28f580c7a`; raw `gradle.properties` at that commit declares Minecraft `1.21.1`, BlockUI `1.0.191-1.21.1-snapshot`, and Domum Ornamentum `1.0.203-1.21.1-snapshot`. |
| BlockUI | Tag `v1.21.1-1.0.199` maps to commit `29c2e55fa09f3d8168dc2677368188d22e2f3688`; raw `gradle.properties` at that commit declares Minecraft `1.21.1` and NeoForge `21.1.113`. |
| Domum Ornamentum | Tag `v1.21.1-1.0.223` maps to commit `fe16a052f1eca2b8a2cc0a0a0ed3354f404c1d84`; raw `gradle.properties` at that commit declares Minecraft `1.21.1`. The jar still uses placeholder metadata version `${file.jarVersion}`. |
| Multi-Piston | Correct source repository is `ldtteam/Piston-Unlimited`, GPL-3.0. Tag `v1.21.1-1.2.51` maps to commit `b74560984ea1da1906e59dd2f34286d55ee30449`; raw `gradle.properties` at that commit declares Minecraft `1.21.1`, BlockUI `1.0.188-1.21.1-snapshot`, and Structurize `1.0.751-1.21.1-snapshot`. |
| Modern Companions 2.0 | CurseForge source link points to `STRHercules/ModernCompanions`, but all public branches checked declare versions below `2.0`; the jar issue tracker points to non-public or missing `MajorBonghits/ModernCompanions`. Exact source commit mapping remains blocked. |
| Waystones | Tag `v21.1.29` maps to annotated tag object `17c30448b26bfd81f7af33e4404677dfc4997b86` and peeled source commit `75f923f36938515571fd71fbe8c30ff8050df417`; raw `gradle.properties` at that commit declares version `21.1.29` and Minecraft `1.21.1`. Runtime-only unless license permission changes; jar metadata says `All Rights Reserved` and GitHub license detection is `NOASSERTION`. |
| Balm | Tag `v21.0.56` maps to annotated tag object `25b904eb951811c98fc90ec2a7760f92ff38c5dc` and peeled source commit `f9af2e38e3a8788d0bddd51de8234ffeb1218ddf`; raw `gradle.properties` at that commit declares version `21.0.56` and Minecraft `1.21.1`. GitHub license detection reports Apache-2.0. |

## Dependency observations

- `scripts/install-runtime-deps.ps1` uses the artifact locators and SHA-256
  values above to install verified runtime jars into Prism LAB. This is runtime
  installation proof only; it does not resolve source-adaptation blockers.
- MineColonies jar metadata requires Structurize, BlockUI, and Domum
  Ornamentum. Its release text also lists Multi-Piston as a required minimum,
  but the jar metadata does not declare a `multipiston` dependency.
- TownTalk is not declared by the selected MineColonies jar metadata and was
  not listed in the verified MineColonies `1.1.1319` dependency minimums.
  Treat TownTalk as a scope question, not part of this selected runtime set,
  until an upstream requirement proves otherwise.
- Modern Companions `2.0` raises its required NeoForge floor to `21.1.215`.
  The selected NeoForge `21.1.233` satisfies that floor.
- Modern Companions `2.0` metadata declares optional Curios, Waystones, WTHIT,
  Balm, and Bad Packets integrations. `runGameTestServer` proved Waystones is
  effectively required for this artifact because automatic subscriber scanning
  loads `WaystoneTeleportEvent$Post`; Waystones then requires Balm.
- Server smoke with CIVITAS `0.1.0-alpha.4` loads Immersive EGO
  `0.1.0-alpha.27` from the Prism LAB mods directory.

## Blocking gaps before source adaptation

1. Map Modern Companions `2.0` to an immutable public source commit, or get an
   explicit source archive from the maintainer.
2. Resolve whether TownTalk remains a CIVITAS requirement even though it is not
   declared by the selected MineColonies runtime artifact.
3. Rebuild Immersive EGO from a clean source state before treating its local jar
   as a reproducible binary prerequisite.

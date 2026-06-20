# CODEX HANDOFF

## Repository state

- Branch: `main`
- Baseline commit before this session: `7b98d412c246ccdc659dfb461b801d8411bb4250`
- State: planning pack plus P0 governance/provenance skeleton, artifact audit,
  and initial NeoForge harness.
- NeoForge build harness exists for `immersive_ego_civitas` version
  `0.1.0-alpha.5`.

## Implemented this session

- Re-read `AGENTS.md`, `TASKS.md`, `docs/CODEX_HANDOFF.md`, and the master prompt.
- Audited first-pass dependency/source facts from primary sources and the local Immersive EGO repository.
- Added repository skeleton files required by P0.
- Added dependency, license, source provenance, compatibility, asset, config, test, performance, and EGO API requirement docs.
- Added `scripts/validate-provenance.ps1` and `.github/workflows/provenance.yml`.
- Updated `TASKS.md` with CIV-001 completion and current P0 blockers.
- Downloaded selected runtime artifacts into ignored `build/audit-artifacts/`,
  extracted mod metadata, and recorded file IDs, sizes, hashes, compatibility,
  and source mapping gaps in `docs/ARTIFACT_AUDIT.md`.
- Added Gradle wrapper, ModDevGradle build, NeoForge metadata template,
  original bootstrap mod class, JUnit identity test, install script, runtime
  dependency installer, and CI build gate.
- Built and installed `immersive_ego_civitas-0.1.0-alpha.4.jar` into the Prism
  LAB `minecraft\mods` folder with SHA-256 match.
- Installed audited runtime dependency jars for MineColonies, Structurize,
  BlockUI, Domum Ornamentum, Multi-Piston, MCA Reborn, Modern Companions,
  Waystones, and Balm into the same LAB mods folder with SHA-256 matches.
- Added local runtime staging and a crash-marker-aware GameTest smoke wrapper.
- Generated the CIVITAS empty GameTest structure and verified the server smoke
  gate with Immersive EGO `0.1.0-alpha.27` from Prism LAB.
- Added a reproducible Prism LAB client smoke wrapper and verified the client
  reaches a responsive Minecraft window with CIVITAS `0.1.0-alpha.4` loaded.
- Mapped Structurize, BlockUI, Domum Ornamentum, Multi-Piston, Waystones, and
  Balm runtime artifacts to immutable Git tags and source commits.
- Added a pinned runtime dependency guard and required both server and client
  smoke gates to find its `pinned runtime dependency check passed` marker.
- Rebuilt the sibling Immersive EGO repository from commit
  `789238c475ecabc19808b9ac7d99df7f457670b8`; the rebuilt
  `immersive_ego-0.1.0-alpha.27.jar` SHA-256 matched the installed Prism LAB
  prerequisite hash `f76dd02414a960a23bb627d59307b9e54f05da1f725adebd2ae3e0ebd8c11329`.
- Added the first original CIVITAS resident identity scaffold:
  `CivitasAuthority`, `ResidentHostKey`, `ResidentRecord`, `ResidentRegistry`,
  and `CivitasResidentSavedData`.
- Added unit tests for resident registry invariants and GameTests for resident
  SavedData lookup and NBT round-trip.
- Hardened the Prism client smoke script against a launch-time `latest.log`
  creation/rotation race.
- Built and installed `immersive_ego_civitas-0.1.0-alpha.5.jar` into the Prism
  LAB `minecraft\mods` folder with SHA-256 match.
- Deep-checked Modern Companions public source evidence again: public branch
  heads are no later than 2026-01-01, public tags stop at `v1.2.0`, and the
  selected CurseForge `2.0` artifact was uploaded on 2026-04-10.

## Exact commands run

```powershell
Get-Content -LiteralPath AGENTS.md -Raw
Get-Content -LiteralPath TASKS.md -Raw
Get-Content -LiteralPath docs\CODEX_HANDOFF.md -Raw
Get-Content -LiteralPath README_IMPORT_FIRST.md -Raw
Get-Content -LiteralPath PACK_SUMMARY.json -Raw
rg --files
Import-Csv -LiteralPath '02_Databases\Roadmap.csv'
Import-Csv -LiteralPath '02_Databases\Backlog.csv'
Import-Csv -LiteralPath '02_Databases\Dependencies.csv'
Import-Csv -LiteralPath '02_Databases\License_Matrix.csv'
Invoke-WebRequest -UseBasicParsing -Uri 'https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml'
Invoke-RestMethod -Uri 'https://api.github.com/repos/ldtteam/minecolonies/releases?per_page=10'
Invoke-RestMethod -Uri 'https://api.github.com/repos/Luke100000/minecraft-comes-alive/releases?per_page=10'
Invoke-RestMethod -Uri 'https://api.github.com/repos/STRHercules/ModernCompanions'
Invoke-RestMethod -Uri 'https://api.github.com/repos/STRHercules/ModernCompanions/releases?per_page=10'
git rev-parse --show-toplevel
git status --short --branch
git rev-parse HEAD
git ls-remote --heads origin main
Invoke-RestMethod -Uri 'https://www.curseforge.com/api/v1/mods/1391597/files/7902593'
Invoke-WebRequest -UseBasicParsing -Uri 'https://www.curseforge.com/api/v1/mods/1391597/files/7902593/download' -OutFile build\audit-artifacts\ModernCompanions-1.21.1-2.0-NeoForge.jar
Get-FileHash -Algorithm SHA256 -LiteralPath build\audit-artifacts\ModernCompanions-1.21.1-2.0-NeoForge.jar
Invoke-RestMethod -Uri 'https://api.github.com/repos/STRHercules/ModernCompanions/branches?per_page=100'
.\gradlew.bat --no-daemon --version
.\gradlew.bat --no-daemon clean build
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\validate-provenance.ps1
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-gametest-smoke.ps1
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-prism-client-smoke.ps1 -TimeoutSeconds 240
pwsh -NoProfile -ExecutionPolicy Bypass -File .\install-mod.ps1 -SkipBuild
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\install-runtime-deps.ps1
git ls-remote --heads --tags https://github.com/ldtteam/Structurize.git
git ls-remote --heads --tags https://github.com/ldtteam/BlockUI.git
git ls-remote --heads --tags https://github.com/ldtteam/Domum-Ornamentum.git
git ls-remote --heads --tags https://github.com/ldtteam/Piston-Unlimited.git
git ls-remote --heads --tags https://github.com/TwelveIterationMods/Waystones.git
git ls-remote --heads --tags https://github.com/TwelveIterationMods/Balm.git
git ls-remote --heads --tags https://github.com/STRHercules/ModernCompanions.git
Invoke-WebRequest -UseBasicParsing -Uri 'https://raw.githubusercontent.com/ldtteam/Structurize/16a05cdb3524fae1662d8bab1f48b1d28f580c7a/gradle.properties'
Invoke-WebRequest -UseBasicParsing -Uri 'https://raw.githubusercontent.com/ldtteam/BlockUI/29c2e55fa09f3d8168dc2677368188d22e2f3688/gradle.properties'
Invoke-WebRequest -UseBasicParsing -Uri 'https://raw.githubusercontent.com/ldtteam/Domum-Ornamentum/fe16a052f1eca2b8a2cc0a0a0ed3354f404c1d84/gradle.properties'
Invoke-WebRequest -UseBasicParsing -Uri 'https://raw.githubusercontent.com/ldtteam/Piston-Unlimited/b74560984ea1da1906e59dd2f34286d55ee30449/gradle.properties'
Invoke-WebRequest -UseBasicParsing -Uri 'https://raw.githubusercontent.com/TwelveIterationMods/Waystones/75f923f36938515571fd71fbe8c30ff8050df417/gradle.properties'
Invoke-WebRequest -UseBasicParsing -Uri 'https://raw.githubusercontent.com/TwelveIterationMods/Balm/f9af2e38e3a8788d0bddd51de8234ffeb1218ddf/gradle.properties'
Invoke-RestMethod -Uri 'https://api.github.com/repos/STRHercules/ModernCompanions/branches?per_page=100'
Invoke-RestMethod -Uri 'https://api.github.com/repos/STRHercules/ModernCompanions/tags?per_page=100'
Invoke-WebRequest -UseBasicParsing -Uri 'https://raw.githubusercontent.com/STRHercules/ModernCompanions/main/gradle.properties'
.\gradlew.bat --no-daemon test
.\gradlew.bat --no-daemon clean build
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\validate-provenance.ps1
pwsh -NoProfile -ExecutionPolicy Bypass -File .\install-mod.ps1 -SkipBuild
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-gametest-smoke.ps1
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\run-prism-client-smoke.ps1 -TimeoutSeconds 240
jar tf "C:\Users\Emmanuel Tremblay\AppData\Roaming\PrismLauncher\instances\1.21.1 TesT LaB\minecraft\mods\immersive_ego_civitas-0.1.0-alpha.5.jar"
```

## Test results

- `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\validate-provenance.ps1` passed.
- Output: `Provenance validation passed. Active adapted-source rows: 0`.
- `.\gradlew.bat --no-daemon test` passed.
- `.\gradlew.bat --no-daemon clean build` passed.
- `scripts\run-gametest-smoke.ps1` passed and `build\gametest-smoke.log`
  contains `All 3 required tests passed :)` and
  `pinned runtime dependency check passed`.
- `scripts\run-prism-client-smoke.ps1 -TimeoutSeconds 240` passed and
  `build\client-smoke-report.json` contains `result=passed`, all CIVITAS and
  client render/audio success markers including the pinned runtime guard marker,
  no failure markers, and no crash reports since launch.
- `.\install-mod.ps1 -SkipBuild` produced `build/install-report.json` with
  `hashMatch=true`, `remainingInstalledJarCount=1`, and installed SHA-256
  `bdf07f1ddb5276883e7a3cc9d2a0ac03af58ed45be9c10f00f0f569ad501909e`.
- The installed alpha.5 jar contains the new `resident` classes and
  `CivitasGameTests.class`.
- `scripts\install-runtime-deps.ps1` produced `build/runtime-deps-report.json`
  with `allHashesMatch=true` and `allSingleInstalled=true`.
- Sibling Immersive EGO `.\gradlew.bat --no-daemon clean build` passed and
  reproduced SHA-256
  `f76dd02414a960a23bb627d59307b9e54f05da1f725adebd2ae3e0ebd8c11329`.
- Sibling Immersive EGO `.\gradlew.bat --no-daemon runGameTestServer` passed
  with 3 required tests.

## Upstream files adapted

- None. Only original CIVITAS bootstrap, runtime guard, resident registry,
  SavedData, and test source has been added.

## Provenance status

- `docs/CODE_ADAPTATION_MANIFEST.csv` added.
- No active adapted-source rows.
- Modern Companions adaptation is blocked pending source/artifact and lineage verification.
- Structurize, BlockUI, Domum Ornamentum, and Multi-Piston source tags are
  mapped, but no source has been adapted yet.
- Waystones is mapped for runtime audit but should be treated as runtime-only
  because jar metadata says `All Rights Reserved` and GitHub license detection
  is `NOASSERTION`.

## Known blockers

- Modern Companions CurseForge `2.0` artifact is hashed, but still does not map
  to an immutable public source commit.
- Modern Companions repository-level license file is not exposed by the GitHub API.
- Human Companions and Basic Weapons lineage audits are required before adapting Modern Companions files.
- TownTalk is documented as an optional, non-selected speech add-on for the
  current release gate; do not bundle or adapt it unless scope changes.

## Risks changed

- R-002 remains critical and is now backed by concrete mismatch evidence.
- R-013 now has a validation script and CI gate.

## Next exact task

Obtain Modern Companions `2.0` source from the maintainer or switch to a
source-mapped Modern Companions version and rerun compatibility tests. Until
then, continue only original CIVITAS-owned work that does not compile against or
adapt Modern Companions implementation details.

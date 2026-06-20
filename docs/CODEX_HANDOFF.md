# CODEX HANDOFF

## Repository state

- Branch: `main`
- Baseline commit before this session: `7b98d412c246ccdc659dfb461b801d8411bb4250`
- State: planning pack plus P0 governance/provenance skeleton, artifact audit,
  and initial NeoForge harness.
- NeoForge build harness exists for `immersive_ego_civitas` version
  `0.1.0-alpha.2`.

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
- Built and installed `immersive_ego_civitas-0.1.0-alpha.2.jar` into the Prism
  LAB `minecraft\mods` folder with SHA-256 match.
- Installed audited runtime dependency jars for MineColonies, Structurize,
  BlockUI, Domum Ornamentum, Multi-Piston, MCA Reborn, and Modern Companions
  into the same LAB mods folder with SHA-256 matches.

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
pwsh -NoProfile -ExecutionPolicy Bypass -File .\install-mod.ps1 -SkipBuild
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\install-runtime-deps.ps1
```

## Test results

- `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\validate-provenance.ps1` passed.
- Output: `Provenance validation passed. Active adapted-source rows: 0`.
- `.\gradlew.bat --no-daemon clean build` passed.
- `.\install-mod.ps1 -SkipBuild` produced `build/install-report.json` with
  `hashMatch=true` and `remainingInstalledJarCount=1`.
- `scripts\install-runtime-deps.ps1` produced `build/runtime-deps-report.json`
  with `allHashesMatch=true` and `allSingleInstalled=true`.

## Upstream files adapted

- None. Only original CIVITAS bootstrap source has been added.

## Provenance status

- `docs/CODE_ADAPTATION_MANIFEST.csv` added.
- No active adapted-source rows.
- Modern Companions adaptation is blocked pending source/artifact and lineage verification.
- Structurize, BlockUI, Domum Ornamentum, and Multi-Piston source adaptation is
  blocked pending file-to-source commit mapping.

## Known blockers

- Modern Companions CurseForge `2.0` artifact is hashed, but must still be
  mapped to an immutable source commit.
- Modern Companions repository-level license file is not exposed by the GitHub API.
- Human Companions and Basic Weapons lineage audits are required before adapting Modern Companions files.
- Structurize, BlockUI, Domum Ornamentum, and Multi-Piston source commits are
  not mapped from their selected CurseForge files yet.
- TownTalk is not part of the selected runtime set unless a later requirement
  proves it mandatory.

## Risks changed

- R-002 remains critical and is now backed by concrete mismatch evidence.
- R-013 now has a validation script and CI gate.

## Next exact task

Run a client or dedicated-server smoke boot in Prism LAB, then continue CIV-003
source mapping before adapting Modern Companions or LDT dependency code.

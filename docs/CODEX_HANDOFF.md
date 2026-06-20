# CODEX HANDOFF

## Repository state

- Branch: `main`
- Baseline commit before this session: `7b98d412c246ccdc659dfb461b801d8411bb4250`
- State: planning pack plus P0 governance/provenance skeleton and artifact
  audit.
- No NeoForge build harness exists yet.

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
```

## Test results

- `pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\validate-provenance.ps1` passed.
- Output: `Provenance validation passed. Active adapted-source rows: 0`.
- No Gradle build exists yet.

## Upstream files adapted

- None.

## Provenance status

- `docs/CODE_ADAPTATION_MANIFEST.csv` added.
- No active adapted-source rows.
- Modern Companions adaptation is blocked pending source/artifact and lineage verification.

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

Finish CIV-003 by mapping Modern Companions `2.0` and the selected LDT
dependency jars to immutable source commits before initializing the NeoForge
harness.

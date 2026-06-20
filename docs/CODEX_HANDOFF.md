# CODEX HANDOFF

## Repository state

- Branch: `main`
- Baseline commit before this session: `7b98d412c246ccdc659dfb461b801d8411bb4250`
- State: planning pack plus P0 governance/provenance skeleton.
- No NeoForge build harness exists yet.

## Implemented this session

- Re-read `AGENTS.md`, `TASKS.md`, `docs/CODEX_HANDOFF.md`, and the master prompt.
- Audited first-pass dependency/source facts from primary sources and the local Immersive EGO repository.
- Added repository skeleton files required by P0.
- Added dependency, license, source provenance, compatibility, asset, config, test, performance, and EGO API requirement docs.
- Added `scripts/validate-provenance.ps1` and `.github/workflows/provenance.yml`.
- Updated `TASKS.md` with CIV-001 completion and current P0 blockers.

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

- Modern Companions CurseForge `2.0` artifact must be mapped to an immutable source commit.
- Modern Companions repository-level license file is not exposed by the GitHub API.
- Human Companions and Basic Weapons lineage audits are required before adapting Modern Companions files.
- TownTalk exact 1.21.1 artifact/source mapping remains unverified.

## Risks changed

- R-002 remains critical and is now backed by concrete mismatch evidence.
- R-013 now has a validation script and CI gate.

## Next exact task

Finish CIV-002/CIV-003 by downloading or otherwise verifying mandatory runtime
artifacts, extracting mod metadata, and mapping every selected jar to a source
commit before initializing the NeoForge harness.

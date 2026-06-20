# Source Provenance

No upstream implementation files have been copied or adapted into CIVITAS.

## Adaptation rule

Before adding any copied or substantially adapted source file:

1. Verify the upstream project license.
2. Verify the exact immutable source commit.
3. Record the original path.
4. Add a row to `docs/CODE_ADAPTATION_MANIFEST.csv`.
5. Add a source header to the adapted destination file.
6. Run `scripts/validate-provenance.ps1`.

## Required source header

```text
CIVITAS ADAPTED SOURCE
Manifest ID: SRC-0000
Original Project: <project>
Original Commit: <commit>
Original Path: <path>
Original License: <SPDX or exact license>
Adaptation Date: YYYY-MM-DD
Summary: <short summary of changes>
```

## Current status

Only original CIVITAS bootstrap source exists. Modern Companions adaptation is
blocked until the CurseForge `2.0` artifact maps to an immutable public source
commit and the Human Companions / Basic Weapons lineage questions are resolved.
Structurize, BlockUI, Domum Ornamentum, and Multi-Piston source commit mappings
are also pending before adapting code from those projects.

# Decisions

## DEC-0001 - Start with P0 governance before code adaptation

Date: 2026-06-20

Status: Accepted

Decision: CIVITAS will not copy or adapt upstream implementation files until
dependency versions, source commits, license evidence, and manifest tooling are
present.

Reason: The project intentionally depends on GPL-family upstream code and forbids
asset copying by default. Early provenance gates are cheaper than repairing an
unclear lineage later.

## DEC-0002 - Treat MineColonies 1.1.1319 as the current stable audit target

Date: 2026-06-20

Status: Accepted

Decision: The first MineColonies source mapping targets release
`v1.21.1-1.1.1319` at commit `35bd7ad7448c562c84d11dc9dff5b067e8f131e5`.
Snapshot `v1.21.1-1.1.1331-snapshot` is recorded but not selected for the first
stable compatibility set.

Reason: The release page identifies 1.1.1319 as a concrete Minecraft 1.21.1
release with dependency minimums. Snapshot adoption should happen only through a
separate compatibility update.

## DEC-0003 - Block Modern Companions adaptation until artifact/source mismatch is resolved

Date: 2026-06-20

Status: Accepted

Decision: CIVITAS may not compile against Modern Companions implementation
details or copy/adapt its source until the CurseForge `2.0` artifact is mapped
to an immutable source commit and the Human Companions / Basic Weapons lineage is
verified.

Reason: The linked GitHub repository release metadata currently tops out at
`v1.2.0`, while the imported baseline and CurseForge page identify a `2.0`
runtime artifact. The GitHub API also returns no root license file for the
Modern Companions repository.

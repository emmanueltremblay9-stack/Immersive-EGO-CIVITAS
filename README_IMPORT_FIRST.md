# Import Immersive EGO: CIVITAS into Notion

## Package contents

- `01_Pages` — masterplan and architecture pages in Markdown.
- `02_Databases` — Notion-ready CSV databases.
- `03_Codex` — master execution prompt and repository templates.
- `04_Branding` — original logo, icon and cover.
- `05_Legal` — GPL provenance and notices templates.
- `06_Reference` — baseline and relation map.
- `07_Project_Control` — offline Excel control workbook.

## Import the pages

Use:

`Settings → Import → Text & Markdown`

Select `Immersive_EGO_CIVITAS_Notion_Pages.zip`.

The Markdown import creates the page hierarchy contained in the ZIP.

## Import the databases

Import each CSV in `02_Databases` separately using:

`Settings → Import → CSV`

or use `/csv` in a Notion page.

Each row becomes a database page and each column becomes a property.

## Rebuild database relations

CSV import does not preserve relations, rollups or formulas. Recreate the links
defined in:

- `01_Pages/18_Notion_Database_Setup.md`
- `06_Reference/NOTION_RELATION_MAP.md`

Recommended relations:

- Backlog → Roadmap
- Backlog → Milestones
- Backlog → Dependencies
- Backlog → Risks
- Backlog → Acceptance Criteria
- Test Cases → Backlog
- Code Adaptation Manifest → Dependencies
- Codex Sessions → Roadmap
- Release Checklist → Milestones

## Add project branding

- Icon: `04_Branding/immersive_ego_civitas_logo_512.png`
- High-resolution logo: `04_Branding/immersive_ego_civitas_logo_1024.png`
- Cover: `04_Branding/immersive_ego_civitas_banner_1600x600.png`

## Start Codex

1. Copy `03_Codex/AGENTS_TEMPLATE.md` into the repository root as `AGENTS.md`.
2. Copy `03_Codex/TASKS_TEMPLATE.md` into the repository root as `TASKS.md`.
3. Copy `03_Codex/CODEX_HANDOFF_TEMPLATE.md` to `docs/CODEX_HANDOFF.md`.
4. Give Codex `03_Codex/IMMERSIVE_EGO_CIVITAS_CODEX_MASTER_PROMPT.txt`.
5. Begin with P0. Do not permit code copying before dependency/source/license
   provenance is complete.

## Baseline warning

Versions in the planning documents are a June 19, 2026 baseline. Codex must
perform a fresh source/artifact audit before adapting code.

# Notion database setup

Import each CSV as its own database.

## Relations to rebuild manually

- Backlog → Milestones
- Backlog → Roadmap
- Backlog → Dependencies
- Backlog → Risks
- Backlog → Acceptance Criteria
- Test Cases → Backlog
- Code Adaptation Manifest → Dependencies
- Codex Sessions → Roadmap
- Release Checklist → Milestones

CSV import cannot preserve Notion relations, rollups or formulas.

## Recommended views

### Backlog

- Board by Status
- Board by Phase
- P0-only table
- Group by Source Project
- Timeline by Milestone

### Risks

- Open critical risks
- Group by Owning Phase
- Sort Impact, then Likelihood

### Code Adaptation Manifest

- Blocked lineage
- Awaiting review
- By upstream project
- `Asset Included = Yes` — expected to be normally empty

## Property mapping

Use ID/name columns as the title property. Convert Status, Priority, Phase, Milestone, Workstream and Source Project into Select properties. Keep `Depends On` as text until database relations are rebuilt.

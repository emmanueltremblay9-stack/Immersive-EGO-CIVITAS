# API contracts

These are architectural contracts, not invented upstream method names.

## Immersive EGO facade

```text
resolve subject
read bounded snapshot
query work/fight/travel/recovery capability
submit civic stimulus
advance unloaded subject when supported
```

## Resident host adapter

```text
host type
entity UUID
loaded state
inventory view
navigation view
identity view
capability set
```

## MineColonies facade

```text
resolve colony
resolve/assign building and home
create/track request
reserve/extract/insert transactionally
read permission and raid state
```

## Provider SPIs

- CurrencyProvider
- DialogueProvider
- WorkRecipeCondition
- ReputationContributor
- CultureModifier
- CrisisIntervention

Codex must inspect the pinned source and implement actual names only after verification.

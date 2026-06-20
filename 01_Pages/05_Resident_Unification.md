# Unified resident model

## Canonical record

```text
UnifiedResidentRecord
├─ schemaVersion
├─ residentId
├─ hostType
├─ hostEntityUuid
├─ MineColonies references
├─ MCA identity and family references
├─ companion/military state
├─ Immersive EGO subject reference
├─ colony, building, home and household
├─ profession, specialization, skills and schedule
├─ faction, culture, loyalty, grievances and reputation references
├─ work assignment and transaction references
└─ simulation tier and timestamps
```

## Host types

- MineColonies citizen
- MCA villager
- Modern Companion

A host may expose several capabilities, but it has exactly one canonical resident record.

## Non-destructive colony recruitment

An MCA villager becomes a colonist by receiving:

- colony membership;
- family-aware housing;
- building and profession assignment;
- request/logistics participation;
- civic permissions and history.

The MCA entity, appearance, identity, relationships and family are preserved.

## Repair policy

- Detect duplicate links.
- Prefer records with valid authoritative references.
- Preserve MCA family identifiers.
- Quarantine ambiguous mappings.
- Offer dry-run and repair commands.
- Log every automated repair.

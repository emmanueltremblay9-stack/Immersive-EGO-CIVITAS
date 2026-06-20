# Masterplan

## Delivery doctrine

Development proceeds through **gated vertical integration**, not horizontal feature accumulation. A phase is complete only when its scenario, persistence, dedicated-server, migration and provenance gates pass.

## Core architecture

```text
MCA host / MineColonies citizen / Modern Companion host
                           ↓
                  ResidentHostAdapter
                           ↓
                UnifiedResidentRecord
          ↙                ↓                 ↘
 MineColonies civic   Immersive EGO       MCA social
 and logistics        body and mind       and family
          ↘                ↓                 ↙
     CIVITAS work, culture, faction, economy,
     defense, governance, migration and crises
```

## Authority boundaries

- **MineColonies:** colony, territory, buildings, housing, requests, warehouse, work orders, permissions and raids.
- **MCA:** identity, appearance, personality, family, relationships, age and dialogue.
- **Immersive EGO:** hydration, nutrition, temperature, sleep, fatigue, stress, origin and readiness.
- **CIVITAS:** canonical civic identity and all cross-system societal state.
- **Modern Companions-derived runtime:** host-neutral military services.

## Source-adaptation strategy

1. Call installed upstream systems directly where their types are sufficiently general.
2. Isolate fragile/internal calls behind exact-version facades.
3. Where code assumes a specific host entity, copy verified GPL source into the CIVITAS namespace and generalize it.
4. Record every copied or substantially adapted file in the Code Adaptation Manifest.
5. Never copy upstream assets merely because code is GPL.

## Critical path

```text
P0 Legal/source audit
→ P1 Integration harness
→ P2 EGO external subject API
→ P3 Unified resident kernel
→ P4 MineColonies civic bridge
→ P5 MCA social bridge
→ P6 Military bridge
→ P7 Desert vertical slice
```

Broad economy, governance and diplomacy work must not begin before the P7 slice passes.

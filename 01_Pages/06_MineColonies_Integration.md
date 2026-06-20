# MineColonies integration

MineColonies is mandatory and remains the civic backbone.

## Direct-use targets

- Colony lookup and territory.
- Building and housing data.
- Colony permissions and lifecycle.
- Request and warehouse state.
- Work orders.
- Raid and guard state.
- Existing blocks, buildings, registered content and assets.

## GPL adaptation targets

Copy and generalize code only when it is too tightly bound to MineColonies citizen entities:

- worker AI state machines;
- job execution paths;
- resource request creation;
- inventory and tool lookup;
- task scheduling;
- skill and experience calculations;
- guard targeting and response logic.

## ExternalCitizenProxy

The proxy supplies a MineColonies-facing view of an external host:

```text
resident identity
colony/building/job
inventory
position/navigation
skills and readiness
home
request state
permission context
```

## Transaction safety

Cross-system logistics must use:

```text
reserve → transfer/execute → consume/commit → release
                 ↘ cancel/rollback/repair
```

Naive extract-and-insert integration is prohibited.

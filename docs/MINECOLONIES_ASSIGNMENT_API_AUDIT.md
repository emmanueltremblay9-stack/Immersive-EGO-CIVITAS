# MineColonies Assignment API Audit

Audit date: 2026-06-20

This audit records the pinned public MineColonies assignment surface needed
before CIVITAS mutates colony housing or work assignments. CIVITAS does not
copy MineColonies implementation source and does not yet mutate colony state.

## Source/runtime evidence

| Project | Pinned source | Installed runtime evidence |
|---|---|---|
| MineColonies | `ldtteam/minecolonies` tag commit `35bd7ad7448c562c84d11dc9dff5b067e8f131e5` | Prism LAB jar `minecolonies-1.1.1319-1.21.1.jar` exposes the public API methods verified by `MineColoniesAssignmentApiContract` |

## Verified assignment surface

Citizen and lookup methods:

- `ICitizenManager#getCivilian(int)`
- `ICitizenData#getHomeBuilding()`
- `ICitizenData#setHomeBuilding(IBuilding)`
- `ICitizenData#getWorkBuilding()`
- `ICitizenData#getJob()`
- `ICitizenData#setJob(IJob)`

Building and module methods:

- `IBuildingModule#getBuilding()`
- `IBuilding#getModulesByType(Class)`
- `IBuilding#getAllAssignedCitizen()`
- `IBuilding#cancelAllRequestsOfCitizenOrBuilding(ICitizenData)`
- `IBuilding#markDirty()`
- `IAssignsCitizen#assignCitizen(ICitizenData)`
- `IAssignsCitizen#removeCitizen(ICitizenData)`
- `IAssignsCitizen#getAssignedCitizen()`
- `IAssignsCitizen#isFull()`
- `IAssignsCitizen#hasAssignedCitizen(ICitizenData)`
- `IAssignsCitizen#hasAssignedCitizen()`
- `IAssignsJob#getJobEntry()`

Job methods:

- `IJob#assignTo(IAssignsJob)`
- `IJob#getWorkBuilding()`
- `IJob#getWorkModule()`
- `IJob#onRemoval()`

## Source behavior summary

- Housing uses `IAssignsCitizen#assignCitizen` through MineColonies living
  modules. The living module assignment hook sets `ICitizenData#setHomeBuilding`
  and recalculates max citizens.
- Work assignment uses `IAssignsJob#assignCitizen` through MineColonies worker
  modules. The worker module creates a job when needed, calls
  `IJob#assignTo(IAssignsJob)`, then records the citizen in the module.
- `IJob#assignTo` validates that the target module job entry matches, removes
  the citizen from the previous work module when moving between buildings, sets
  the work building/module fields, and calls `ICitizenData#setJob`.
- Removal calls route through the assignment module and job removal hooks, so
  CIVITAS must avoid direct field mutation or partial assignment changes.

## CIVITAS rule

The first CIVITAS mutation path must call MineColonies assignment modules rather
than setting citizen fields directly:

- use `IAssignsCitizen#assignCitizen` for home assignment;
- use `IAssignsJob#assignCitizen` for work assignment;
- treat false return values as no-op failures and leave CIVITAS resident state
  repairable;
- do not bypass MineColonies request cleanup, dirty marking, or job removal
  hooks.

`MineColoniesAssignmentApiContract` verifies these installed runtime members
during the GameTest smoke gate.

## CIVITAS executor status

Alpha.14 adds `MineColoniesAssignmentService`, `MineColoniesAssignmentPlan`, and
`MineColoniesAssignmentResult`.

- The executor calls `assignCitizen`, `removeCitizen`, and
  `hasAssignedCitizen` through the verified assignment modules.
- It treats assignment rejection as a failed no-op.
- If a downstream work assignment fails after a home assignment changed, it
  removes the target home assignment and restores the previous home module when
  one is provided.
- If a home or work assignment mutates and then throws, it removes the target
  assignment and restores the previous module when one is provided.
- It still does not discover live building modules or mutate real colony state
  from gameplay code; that remains the next slice.

Alpha.17 adds `MineColoniesAssignmentModuleLocator`,
`MineColoniesAssignmentResolution`, and `MineColoniesAssignmentCoordinator`.

- The locator loads the verified `IAssignsCitizen` and `IAssignsJob`
  interfaces from the runtime classloader.
- For home assignment it asks the target building for `IAssignsCitizen`
  modules and excludes modules that also implement `IAssignsJob`, avoiding
  worker modules as housing targets.
- For work assignment it asks the target building for `IAssignsJob` modules.
- Target selection succeeds only when the module is already assigned to the
  citizen or when exactly one open target module exists.
- Previous home modules are discovered from the citizen's current home
  building; previous work modules are discovered from `getJob().getWorkModule()`
  so the executor can restore them on rollback.
- The coordinator returns a normal assignment failure for unresolved or
  ambiguous modules and otherwise routes the plan through
  `MineColoniesAssignmentService`.
- CIVITAS still does not directly set MineColonies citizen home/job fields.

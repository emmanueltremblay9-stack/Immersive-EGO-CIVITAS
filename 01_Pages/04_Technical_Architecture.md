# Technical architecture

## Primary services

```text
ResidentRegistry
ResidentHostAdapterRegistry
ColonyFacade
BuildingAssignmentFacade
HousingFacade
RequestSystemFacade
WarehouseQueryFacade
McaSocialFacade
McaFamilyFacade
CivitasEgoFacade
MilitaryRoleService
OrderCoordinator
WorkPlanner
TransactionLedger
FactionService
ReputationService
EconomyService
CrisisEngine
CivicSimulationScheduler
```

## Persistence

Use versioned `SavedData` for canonical civic records and global indices. Loaded host entities carry only a fast reference to the canonical resident ID.

The same data must never be stored independently in three host systems.

## Simulation tiers

| Tier | Behavior |
|---|---|
| Near/active | Detailed AI, pathfinding, work and combat |
| Loaded/far | Aggregated decisions at longer intervals |
| Unloaded | Time-advanced household/economic state |
| Settlement aggregate | Population, stock, crisis and reputation summaries |

## Networking

The server is authoritative. Clients receive bounded dossier/dashboard data and deltas. Every command, storage action, military order and private social query is revalidated server-side.

## Version policy

Internal MineColonies and other fragile calls are isolated and pinned. Unknown mandatory-mod versions should fail with a clear compatibility message rather than silently changing or corrupting world data.

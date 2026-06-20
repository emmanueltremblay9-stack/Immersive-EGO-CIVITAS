# Upstream Resident API Audit

Audit date: 2026-06-20

This audit records the pinned public resident identity surfaces used by the
alpha.8 CIVITAS host adapters. The adapters are original CIVITAS source and use
only public class and method names as strings; no upstream implementation source
or assets were copied.

## Source/runtime evidence

| Project | Pinned source | Installed runtime evidence |
|---|---|---|
| MCA Reborn | `Luke100000/minecraft-comes-alive` tag commit `802ab602a7e2aea6284853722ffde88f23cd6840` | Prism LAB jar `mca-neoforge-7.7.11+1.21.1.jar` contains `net/conczin/mca/entity/VillagerEntityMCA.class`, `net/conczin/mca/entity/VillagerLike.class`, and `net/conczin/mca/server/world/data/FamilyTreeNode.class` |
| MineColonies | `ldtteam/minecolonies` tag commit `35bd7ad7448c562c84d11dc9dff5b067e8f131e5` | Prism LAB jar `minecolonies-1.1.1319-1.21.1.jar` contains `com/minecolonies/api/entity/citizen/AbstractEntityCitizen.class`, `com/minecolonies/api/colony/ICitizenData.class`, `com/minecolonies/api/colony/ICitizen.class`, and `com/minecolonies/api/colony/IColony.class` |

## Verified public identity members

MCA Reborn:

- `net.conczin.mca.entity.VillagerEntityMCA#getUUID()`
- `net.conczin.mca.entity.VillagerEntityMCA#getRelationships()`
- `net.conczin.mca.entity.VillagerEntityMCA#getResidency()`
- `net.conczin.mca.entity.VillagerEntityMCA#getGenetics()`
- `net.conczin.mca.entity.VillagerEntityMCA#getAgeState()`
- `net.conczin.mca.server.world.data.FamilyTreeNode#id()`
- `FamilyTreeNode#father()`, `mother()`, `partner()`, `children()`, and `gender()`

MineColonies:

- `com.minecolonies.api.colony.ICitizen#getId()`
- `ICitizen#getColony()`
- `ICitizen#getName()`
- `ICitizen#isChild()`
- `com.minecolonies.api.colony.IColony#getID()`
- `IColony#getCitizenManager()`
- `com.minecolonies.api.colony.ICitizenData#getEntity()`
- `ICitizenData#getHomeBuilding()`
- `ICitizenData#getWorkBuilding()`
- `ICitizenData#getPartner()`, `getChildren()`, `getSiblings()`, and `getParents()`
- `com.minecolonies.api.entity.citizen.AbstractEntityCitizen#getCitizenData()`
- `AbstractEntityCitizen#getCitizenColonyHandler()`
- `AbstractEntityCitizen#getCivilianID()`
- `com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenColonyHandler#getColonyId()`

## CIVITAS host-key rules

- MCA authority host keys use `villager_entity:<entity-uuid>`.
- MineColonies authority host keys use `colony:<colony-id>/citizen:<citizen-id>`.
- MineColonies IDs must both be positive; unregistered entities or data do not
  produce host keys.
- Adapters perform read-only reflection and do not create family tree nodes,
  colonies, citizens, buildings, or relationships.

## Verification commands

```powershell
javap -classpath "<Prism LAB mods>\mca-neoforge-7.7.11+1.21.1.jar" -public net.conczin.mca.entity.VillagerEntityMCA
javap -classpath "<Prism LAB mods>\mca-neoforge-7.7.11+1.21.1.jar" -public net.conczin.mca.server.world.data.FamilyTreeNode
javap -classpath "<Prism LAB mods>\minecolonies-1.1.1319-1.21.1.jar" -public com.minecolonies.api.colony.ICitizen
javap -classpath "<Prism LAB mods>\minecolonies-1.1.1319-1.21.1.jar" -public com.minecolonies.api.colony.ICitizenData
javap -classpath "<Prism LAB mods>\minecolonies-1.1.1319-1.21.1.jar" -public com.minecolonies.api.entity.citizen.AbstractEntityCitizen
```

`UpstreamResidentApiContract` now verifies the installed runtime still exposes
the class and method surface above during the GameTest smoke gate.

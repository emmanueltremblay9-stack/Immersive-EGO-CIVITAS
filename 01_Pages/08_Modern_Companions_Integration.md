# Modern Companions integration

Modern Companions is mandatory, but its content assets and separate entity ecosystem are not duplicated.

## Adapt into host-neutral services

- Follow, guard, patrol, escort, hold and retreat.
- Equipment and weapon evaluation.
- Combat roles.
- XP and leveling.
- Traits.
- Morale and bond.
- Friendly-fire policy.
- Safe recall.
- Support behavior.

## Exclude by default

- Soul Gems and summoning tools.
- Spawn gems.
- Custom weapons and structures.
- Textures, models, sounds and GUI art.
- Any file whose Human Companions or Basic Weapons lineage is unresolved.

## AI ownership

A single order coordinator arbitrates behavior:

```text
EMERGENCY
> DEFENSE
> AUTHORIZED PLAYER ORDER
> ESSENTIAL EGO NEED
> WORK
> FAMILY/SOCIAL
> IDLE
```

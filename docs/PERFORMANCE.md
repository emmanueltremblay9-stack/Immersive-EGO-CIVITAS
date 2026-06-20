# Performance

No runtime simulation exists yet.

Design constraints:

- no full-world scans;
- no per-tick warehouse scans;
- bounded pathfinding requests;
- bounded history;
- server-authoritative delta networking;
- simulation tiers for active, loaded-far, unloaded, and settlement aggregate
  state;
- profiler counters for resident, transaction, pathfinding, and cache behavior.

The first playable target must document performance with at least 100 active
residents after the unified resident kernel and MineColonies bridge exist.

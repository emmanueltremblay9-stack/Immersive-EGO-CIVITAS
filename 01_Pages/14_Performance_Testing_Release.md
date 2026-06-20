# Performance, testing and release

## Performance rules

- No full-world scans.
- No per-tick warehouse or inventory rescans.
- Bounded pathfinding requests.
- Cached colony/building/host lookups.
- Simulation tiers.
- Bounded history.
- Delta networking.
- Profiler counters and configurable tick budgets.

## Test hierarchy

1. Pure Java tests.
2. NeoForge GameTests.
3. Cross-mod integration tests.
4. Client and dedicated-server smoke tests.
5. Long soak tests.
6. Transaction/duplication stress tests.
7. Save migration and rollback tests.
8. Compatibility matrix.
9. Legal/provenance and forbidden-asset CI.

## Release gate

No binary release without corresponding source, completed adaptation manifest, dependency matrix, asset audit and supported-version declaration.

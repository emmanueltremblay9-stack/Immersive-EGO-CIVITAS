# Implementation Log

## 2026-06-20

- Continued from the imported planning pack.
- Re-read `AGENTS.md`, `TASKS.md`, `docs/CODEX_HANDOFF.md`, and the master
  prompt.
- Audited the first P0 dependency/source facts from primary sources and local
  repositories.
- Added P0 repository skeleton docs and provenance/asset validation tooling.
- Downloaded selected runtime jars into ignored `build/audit-artifacts/`,
  extracted mod metadata, recorded SHA-256 hashes, and documented source
  mapping blockers in `docs/ARTIFACT_AUDIT.md`.
- Initialized the NeoForge 1.21.1 Java 21 harness for `immersive_ego_civitas`
  and advanced it to version `0.1.0-alpha.3`.
- Added original bootstrap source only; no upstream implementation code or
  assets were copied.
- Added install verification scripts for the CIVITAS jar and audited runtime
  dependency jars.
- Added a minimal original CIVITAS GameTest smoke gate and a local runtime
  staging script for Gradle runs against the verified LAB dependency stack.
- Added a smoke wrapper that fails on Minecraft crash markers because
  ModDevGradle can report success after a mod-loading crash.
- Identified Modern Companions' Waystones event subscriber as a runtime crash
  source when Waystones is absent; pinned Waystones and Balm runtime support
  jars.
- Added `data/immersive_ego_civitas/structure/empty.nbt` and verified
  `runGameTestServer` reports `All 1 required tests passed :)`.
- Added a Prism LAB client smoke wrapper that launches `1.21.1 TesT LaB`
  with quoted Prism CLI arguments, checks timestamped client log markers, and
  cleans up only the launched LAB client process.
- Verified `scripts\run-prism-client-smoke.ps1 -TimeoutSeconds 240` reported
  `result=passed` with CIVITAS `0.1.0-alpha.3`, LWJGL, OpenAL, sound engine,
  block atlas, a responsive `Minecraft NeoForge* 1.21.1` window, and no crash
  reports.
- Advanced CIV-003 by mapping Structurize, BlockUI, Domum Ornamentum,
  Multi-Piston, Waystones, and Balm runtime artifacts to immutable Git tags and
  source commits. Modern Companions `2.0` remains source-blocked.
- Added an original pinned runtime dependency guard for the audited LAB stack,
  advanced CIVITAS to `0.1.0-alpha.4`, and verified both server and Prism
  client smoke gates find `pinned runtime dependency check passed`.
- Updated the current Immersive EGO LAB prerequisite to
  `immersive_ego-0.1.0-alpha.27.jar` with SHA-256
  `f76dd02414a960a23bb627d59307b9e54f05da1f725adebd2ae3e0ebd8c11329`.
- Rebuilt the sibling Immersive EGO source at commit
  `789238c475ecabc19808b9ac7d99df7f457670b8`; `.\gradlew.bat --no-daemon clean build`
  reproduced the installed alpha.27 jar SHA-256, and
  `.\gradlew.bat --no-daemon runGameTestServer` passed with 3 required tests.
- Refreshed Modern Companions provenance from GitHub, CurseForge, and Modrinth:
  the selected `2.0` CurseForge artifact still has no matching public source
  tag/release/branch, while official GitHub and Modrinth releases stop at
  `v1.2.0`.
- Resolved the TownTalk scope question for the current release gate: TownTalk
  `1.2.0` is an optional All Rights Reserved / GitHub `NOASSERTION` speech
  add-on, not declared by selected MineColonies metadata and not one of the
  four required CIVITAS pillars, so it is documented as non-selected.
- Re-checked Modern Companions public provenance after the TownTalk decision:
  public branch heads are no later than 2026-01-01, public tags stop at
  `v1.2.0`, and the selected CurseForge `2.0` artifact was uploaded on
  2026-04-10, so Modern Companions remains the only source-adaptation blocker.
- Added the first original resident identity scaffold: canonical resident
  records, host authority keys, reverse host lookup, collision protection, and
  `SavedData` persistence.
- Advanced CIVITAS to `0.1.0-alpha.5`, hardened the Prism client smoke log
  reader against launch-time `latest.log` rotation, installed the verified jar
  into Prism LAB, and verified unit tests, clean build, provenance validation,
  server GameTests, and Prism client smoke.
- Added neutral resident host-adapter abstractions and an identity service for
  future host bindings without importing upstream APIs, advanced CIVITAS to
  `0.1.0-alpha.6`, installed the verified jar into Prism LAB, and verified
  unit tests, clean build, provenance validation, server GameTests, and Prism
  client smoke.
- Inspected pinned MCA Reborn and MineColonies source/runtime classes for
  resident identity surfaces, then added original reflection-backed resident
  host adapters and a runtime API contract GameTest for alpha.8. The MCA
  adapter identifies `VillagerEntityMCA`/`VillagerLike` hosts by `getUUID()`;
  the MineColonies adapter identifies `ICitizen`/`ICitizenData` hosts by
  `getColony().getID()` plus `getId()` and entity fallbacks by
  `getCitizenColonyHandler().getColonyId()` plus `getCivilianID()`.
- Added original alpha.9 recruitment orchestration:
  `ResidentRecruitmentService` requires an MCA host and a MineColonies host,
  merges their exact host keys into one canonical resident record, and rejects
  cross-resident host conflicts or reversed upstream host roles.
- Inspected pinned MineColonies source/runtime assignment surfaces and added
  original alpha.10 `MineColoniesAssignmentApiContract` coverage for public
  home, work, assignment-module, and job mutation methods. CIVITAS still does
  not mutate MineColonies state.
- Advanced CIVITAS to `0.1.0-alpha.10`, installed the verified jar into Prism
  LAB, and verified unit tests, clean build, provenance validation, server
  GameTests, and Prism client smoke. The installed jar SHA-256 is
  `12892d66a42323587202d2040fb355ce1d43ecc1c4a19ecd673bd0eefe6095e2`.
- Added original alpha.14 repairable MineColonies assignment execution:
  `MineColoniesAssignmentPlan`, `MineColoniesAssignmentResult`, and
  `MineColoniesAssignmentService`. The executor invokes assignment modules
  reflectively and rolls back target home/work module changes on downstream
  failures, including partial home/work mutations that throw, instead of
  directly setting MineColonies citizen fields.
- Refreshed the exact runtime dependency guard to the current Prism LAB
  Immersive EGO prerequisite `0.1.0-alpha.29`. The sibling
  `immersive_ego-0.1.0-alpha.29.jar` build artifact hash matches the installed
  jar, but the sibling source checkout is dirty, so immutable source mapping is
  blocked.
- Advanced CIVITAS to `0.1.0-alpha.14`, installed the verified jar into Prism
  LAB, and verified unit tests, clean build, provenance validation, server
  GameTests, and Prism client smoke. The installed jar SHA-256 is
  `9b237d04a53e50de9358e3c31456e49c42c5d3372da7d44b9e9583e9ee6fa495`.
- Added original alpha.18 MineColonies assignment module discovery:
  `MineColoniesAssignmentModuleLocator`, `MineColoniesAssignmentResolution`,
  and `MineColoniesAssignmentCoordinator`. The locator discovers live
  building-object home/work modules through the verified public
  `IAssignsCitizen` and `IAssignsJob` interfaces, rejects ambiguous targets,
  captures previous modules for rollback, and routes resolved plans through the
  repairable assignment executor.
- Refreshed the exact runtime dependency guard to the current Prism LAB
  Immersive EGO prerequisite `0.1.0-alpha.32`. The sibling
  `immersive_ego-0.1.0-alpha.32.jar` build artifact hash matches the installed
  jar, but the sibling source checkout is dirty, so immutable source mapping is
  blocked.
- Advanced CIVITAS to `0.1.0-alpha.18`, installed the verified jar into Prism
  LAB, and verified unit tests, clean build, provenance validation, server
  GameTests, and Prism client smoke. The installed jar SHA-256 is
  `c6b4ef79e8894fdcf2e5474271e126a4d26e4ed55fea339bfb90ce5f9391951b`.
- Refreshed the exact runtime dependency guard to the current Prism LAB
  Immersive EGO prerequisite `0.1.0-alpha.33`. The sibling
  `immersive_ego-0.1.0-alpha.33.jar` build artifact hash matches the installed
  jar, but the sibling source checkout is dirty, so immutable source mapping is
  blocked.
- Advanced CIVITAS to `0.1.0-alpha.19`, installed the verified jar into Prism
  LAB, and verified unit tests, clean build, provenance validation, server
  GameTests, and Prism client smoke. The installed jar SHA-256 is
  `5620ebd9c166140bbad097b474649bb8242266fcc6f651c0f04ed4c54272aac5`.
- No upstream implementation source was copied or adapted.

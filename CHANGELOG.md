# Changelog

## 0.1.0-alpha.21

- Added original linked-resident assignment trigger surface:
  `LinkedResidentAssignmentService`, `ResidentDirectory`, and
  `MineColoniesAssignmentGateway`.
- The trigger refuses to call the MineColonies assignment gateway unless the
  MineColonies citizen already resolves to a CIVITAS resident that also has an
  MCA Reborn host link.
- Added unit coverage and a server GameTest guard for unlinked, half-linked,
  and fully linked assignment trigger behavior.
- Refreshed the exact runtime guard and metadata floor for the current Prism
  LAB Immersive EGO prerequisite, `0.1.0-alpha.35`.
- Rebuilt and installed `immersive_ego_civitas-0.1.0-alpha.21.jar`; unit tests,
  clean build, provenance validation, server GameTests, and Prism client smoke
  passed.
- The installed CIVITAS jar SHA-256 is
  `b1fa1197211a4ad16740a2746ba0003935ea11991c293f08301c2647bab7b1ca`.
- No upstream implementation source or assets were copied or adapted.

## 0.1.0-alpha.19

- Refreshed the exact runtime guard and metadata floor for the current Prism
  LAB Immersive EGO prerequisite, `0.1.0-alpha.33`.
- Rebuilt and installed `immersive_ego_civitas-0.1.0-alpha.19.jar`; unit tests,
  clean build, provenance validation, server GameTests, and Prism client smoke
  passed.
- The installed CIVITAS jar SHA-256 is
  `5620ebd9c166140bbad097b474649bb8242266fcc6f651c0f04ed4c54272aac5`.
- No upstream implementation source or assets were copied or adapted.

## 0.1.0-alpha.18

- Added original MineColonies assignment module discovery:
  `MineColoniesAssignmentModuleLocator`,
  `MineColoniesAssignmentResolution`, and
  `MineColoniesAssignmentCoordinator`.
- Refreshed the exact runtime guard and metadata floor for the current Prism
  LAB Immersive EGO prerequisite, `0.1.0-alpha.32`.
- The locator loads the verified `IAssignsCitizen` and `IAssignsJob` runtime
  interfaces, selects unambiguous home/work assignment modules from live
  building objects, captures previous modules for rollback, and rejects
  ambiguous or full targets instead of guessing.
- The coordinator routes resolved plans through
  `MineColoniesAssignmentService`; CIVITAS still does not directly set
  MineColonies citizen home/job fields.
- Added unit coverage for living-module selection, worker-module exclusion from
  home assignment, previous work module rollback discovery, ambiguous-module
  rejection, and coordinator rollback behavior.
- Rebuilt and installed `immersive_ego_civitas-0.1.0-alpha.18.jar`; unit tests,
  clean build, provenance validation, server GameTests, and Prism client smoke
  passed.
- No upstream implementation source or assets were copied or adapted.

## 0.1.0-alpha.14

- Added an original repairable MineColonies assignment executor:
  `MineColoniesAssignmentPlan`, `MineColoniesAssignmentResult`, and
  `MineColoniesAssignmentService`.
- Refreshed the exact runtime guard and metadata floor for the current Prism
  LAB Immersive EGO prerequisite, `0.1.0-alpha.29`.
- The executor uses assignment modules only (`assignCitizen`,
  `removeCitizen`, and `hasAssignedCitizen`) and does not directly mutate
  MineColonies citizen fields.
- Added unit coverage for home/work assignment, home rollback when work rejects
  a citizen, preserving pre-existing home assignment, partial home/work
  mutation rollback after an exception, and invalid plan rejection.
- Rebuilt and installed `immersive_ego_civitas-0.1.0-alpha.14.jar`; unit tests,
  clean build, provenance validation, server GameTests, and Prism client smoke
  passed.
- No upstream implementation source or assets were copied or adapted.

## 0.1.0-alpha.10

- Added an original MineColonies assignment API contract for the public home,
  work, assignment-module, and job methods that CIVITAS must use before
  mutating colony state.
- Added a GameTest that verifies the installed MineColonies runtime still
  exposes that assignment surface.
- Added `docs/MINECOLONIES_ASSIGNMENT_API_AUDIT.md` with the pinned source and
  runtime evidence for the safe assignment sequence.
- Rebuilt and installed `immersive_ego_civitas-0.1.0-alpha.10.jar`; unit tests,
  clean build, provenance validation, server GameTests, and Prism client smoke
  passed.
- No upstream implementation source or assets were copied or adapted.

## 0.1.0-alpha.9

- Added original resident recruitment orchestration that merges a verified MCA
  host key and a verified MineColonies host key into one canonical CIVITAS
  resident record.
- Added a key-based identity merge path so recruitment can link exact upstream
  hosts without reassigning ambiguous argument roles.
- Added unit coverage for MCA-to-colony resident linking, cross-resident host
  conflict rejection, and reversed upstream host rejection.
- Rebuilt and installed `immersive_ego_civitas-0.1.0-alpha.9.jar`; unit tests,
  clean build, provenance validation, server GameTests, and Prism client smoke
  passed.
- No upstream implementation source or assets were copied or adapted.

## 0.1.0-alpha.8

- Added original reflection-backed MCA Reborn and MineColonies resident host
  adapters using verified public class and method names from pinned upstream
  source/runtime jars.
- Hardened reflective method access so relaxed access is best-effort while
  public upstream methods remain invokable if a classloader rejects it.
- Added a runtime resident API contract GameTest for the installed MCA and
  MineColonies surfaces needed by `CIV-058`.
- Added unit coverage for MCA villager UUID host keys, MineColonies
  `colony:<id>/citizen:<id>` host keys, entity/data fallback, and
  unregistered-citizen rejection.
- No upstream implementation source or assets were copied or adapted.

## 0.1.0-alpha.6

- Added original host-adapter abstractions:
  `ResidentHostAdapter`, `ResidentHostAdapterRegistry`, and
  `ResidentIdentityService`.
- Added unit coverage proving duplicate adapter rejection, unsupported-host
  rejection, and one canonical resident record across multiple supported host
  keys.
- Rebuilt and installed `immersive_ego_civitas-0.1.0-alpha.6.jar`; server and
  Prism client smoke gates passed with the pinned runtime guard.
- No upstream APIs or implementation details were imported.

## 0.1.0-alpha.5

- Added the first original CIVITAS resident identity core:
  `CivitasAuthority`, `ResidentHostKey`, `ResidentRecord`, `ResidentRegistry`,
  and `CivitasResidentSavedData`.
- Added unit coverage for canonical resident host indexing and reassignment
  guards.
- Added GameTests for resident SavedData lookup and NBT round-trip behavior.
- Hardened the Prism client smoke script against transient `latest.log`
  creation/rotation during launch.
- Rebuilt and installed `immersive_ego_civitas-0.1.0-alpha.5.jar`; server and
  Prism client smoke gates passed with the pinned runtime guard.
- Deep-checked Modern Companions public source evidence again. Public GitHub
  branch heads still predate the April 10, 2026 CurseForge `2.0` upload and
  public tags stop at `v1.2.0`, so source mapping remains blocked.

## 0.1.0-alpha.4

- Added an original pinned runtime dependency guard that verifies the audited
  LAB versions at common setup and fails with a CIVITAS-specific error when a
  mandatory mod is missing or drifted.
- Bumped the harness to `0.1.0-alpha.4`, rebuilt and installed the verified
  Prism LAB jar, and confirmed the installed jar contains the alpha.27
  Immersive EGO pin.
- Updated the server and Prism client smoke wrappers to require the pinned
  runtime guard marker before passing.
- Updated the audit docs to reflect the current Immersive EGO LAB prerequisite
  `immersive_ego-0.1.0-alpha.27.jar`.
- Replaced the Immersive EGO dirty-worktree blocker with commit
  `789238c475ecabc19808b9ac7d99df7f457670b8` after a clean sibling rebuild
  reproduced the installed alpha.27 jar hash.
- Resolved TownTalk as an optional, non-selected MineColonies speech add-on for
  the current release gate.

## 0.1.0-alpha.3

- Added a minimal NeoForge GameTest smoke gate for the original CIVITAS bootstrap.
- Added local runtime dependency staging for Gradle smoke runs against the Prism
  LAB dependency stack.
- Added a crash-marker-aware GameTest smoke wrapper and pinned Waystones/Balm
  as Modern Companions runtime support jars.
- Added a reproducible Prism LAB client smoke wrapper and verified CIVITAS
  `0.1.0-alpha.3` reaches a responsive Minecraft client window.
- Mapped Structurize, BlockUI, Domum Ornamentum, Multi-Piston, Waystones, and
  Balm runtime artifacts to immutable source tags/commits.

## 0.1.0-alpha.2

- Initialized the NeoForge 1.21.1 Java 21 harness.
- Added original CIVITAS bootstrap class and metadata template.
- Added Gradle build, CI build gate, provenance validation, install script, and
  runtime dependency installer.
- Installed the verified LAB jar and audited runtime dependency stack.

## Unreleased

- Imported the planning, Notion, branding, legal, and project-control pack.
- Added the P0 governance skeleton, dependency audit, provenance manifest, and asset/provenance validation gate.

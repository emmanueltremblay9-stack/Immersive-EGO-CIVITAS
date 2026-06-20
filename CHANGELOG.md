# Changelog

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

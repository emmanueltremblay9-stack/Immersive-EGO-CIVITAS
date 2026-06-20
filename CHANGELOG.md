# Changelog

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

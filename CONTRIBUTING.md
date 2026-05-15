# Contributing to Woodwalkers

First off, thank you for considering contributing to Woodwalkers! Every bit of help — whether it's a bug report, a translation, or a code change — is genuinely appreciated.

## Table of Contents

- [Ways to Contribute](#ways-to-contribute)
- [Reporting Bugs](#reporting-bugs)
- [Translations](#translations)
- [Development Setup](#development-setup)
- [Project Architecture](#project-architecture)
- [Making Changes](#making-changes)
- [Updating to a New Minecraft Version](#updating-to-a-new-minecraft-version)
- [CI / GitHub Actions](#ci--github-actions)
- [Code Style](#code-style)
- [License](#license)

---

## Ways to Contribute

- **Bug reports** – Open an [issue](https://github.com/ToCraft/woodwalkers-mod/issues) describing what went wrong.
- **Translations** – Add or improve language files under `common/src/main/resources/assets/walkers/lang/`.
- **Bug fixes & features** – Fork the repo, make your changes, and open a pull request against `dev`.
- **Financial support** – If you'd like to support the project financially, you can donate via [Patreon](https://www.patreon.com/tocraft).

---

## Reporting Bugs

Before opening a new issue, please search existing issues (**including closed**) to avoid duplicates. When you do file a bug report, include:

- The exact mod version (e.g. `8.0`) and the Minecraft version.
- Whether you're on Fabric or NeoForge, and the loader version.
- The version of **CraftedCore** you have installed.
- A clear description of what you expected to happen and what actually happened.
- Relevant log output or a crash report where applicable.

---

## Translations

Language files live at:

```
common/src/main/resources/assets/walkers/lang/<locale>.json
```

Copy `en_us.json` as a starting point, rename it to your locale code (e.g. `de_de.json`), translate the values, and open a pull request. No Java knowledge is required for translations.

---

## Development Setup

### Prerequisites

| Tool | Minimum version |
|------|----------------|
| JDK  | 25             |
| Git  | any recent     |

An IDE with Gradle support (IntelliJ IDEA is recommended) will make things easier.

### Cloning and building

```bash
git clone https://github.com/ToCraft/woodwalkers-mod.git
cd woodwalkers-mod

# Build both Fabric and NeoForge jars
./gradlew build
```

The Gradle build is powered by the [ModMaster] plugin. Most configuration lives in `gradle.properties` at the root of the repository — Minecraft version, loader versions, and the `craftedcore_version` dependency are all declared there.

### Key `gradle.properties` values

| Property | Purpose |
|----------|---------|
| `minecraft` | Target Minecraft version |
| `mod_version` | Mod version to publish |
| `craftedcore_version` | Required version of CraftedCore |
| `fabric_loader` / `fabric` / `neoforge` | Loader and platform versions |
| `java` | Java toolchain version |

---

## Project Architecture

The repository uses a multi-module Gradle layout:

```
woodwalkers-mod/
├── common/          # Shared mod code (platform-agnostic)
├── fabric/          # Fabric-specific sources and entrypoints
├── neoforge/        # NeoForge-specific sources and entrypoints
├── assets/          # Repository artwork / screenshots
└── gradle.properties
```

The build system is the [ModMaster] Gradle plugin (`dev.tocraft.modmaster.root`), which wires up multiloader compilation, versioning, and publishing in a consistent way across all of ToCraft's mods.

**The build system should not changed. You can however, contribute to [ModMaster] in order to adapt it to your needs or to update the [NeoForge ModDev](https://docs.neoforged.net/toolchain/docs/plugins/mdg/) or [Fabric Loom](https://docs.fabricmc.net/develop/loom/) backend.**

The mod depends on [CraftedCore], which is resolved from the ToCraft Maven repository (`https://maven.tocraft.dev/public`) using the `craftedcore_version` property.

**Before updating Woodwalkers to a new Minecraft version, update [CraftedCore] *first*. Do not use *any* other library than [CraftedCore].**

---

## Making Changes

1. **Fork** the repository and create a feature branch off `main`.
2. Make your changes in the appropriate module (`common` for shared logic, `fabric`/`neoforge` for platform-specific code).
3. Build locally with `./gradlew build` to make sure everything compiles.
4. **Test every change you do!** Ensure you test it in a regular launcher and on both, the client *and* the server.
4. Open a **pull request** against the `main` branch with a clear description of what the PR does and why.

Please keep pull requests focused — one logical change per PR makes review much easier.

---

## Updating to a New Minecraft Version

Because Woodwalkers sits at the end of a dependency chain, a Minecraft version bump must happen in the correct order. Skipping steps will cause compilation failures.

```
1. ModMaster  →  2. CraftedCore  →  3. woodwalkers-mod
```

**Step 1 — Update [ModMaster]**

ModMaster is the Gradle plugin that drives the entire build. It must be updated first to support the new MC version (new ModDev & Fabric Loom versions, new Java version, etc.). Commit to [ModMaster] first before altering Woodwalkers.

**Step 2 — Update [CraftedCore]**

Once a compatible ModMaster version is available, CraftedCore must be compiled against the new Minecraft version and published to the ToCraft Maven. Wait for a new CraftedCore release before touching Woodwalkers or port [CraftedCore] first.

**Step 3 — Update woodwalkers-mod**

With both upstreams updated, open `gradle.properties` and bump:

```properties
# ModMaster plugin version in build.gradle.kts
id("dev.tocraft.modmaster.root") version ("2.X")

# In gradle.properties
e
craftedcore_version=<new version>
```

Then fix any compilation errors caused by Mojang API changes, run `./gradlew build`, test it and open a pull request.

---

## CI / GitHub Actions

The repository uses two reusable actions maintained in the ToCraft organisation:

| Action | Purpose |
|--------|---------|
| [`modmaster-build-action`](https://github.com/ToCraft/modmaster-build-action) | Triggered on every push/PR — compiles and runs checks |
| [`modmaster-release-action`](https://github.com/ToCraft/modmaster-release-action) | Triggered on releases — publishes to CurseForge, Modrinth, and the ToCraft Maven |

You do not need to modify the workflow files for normal contributions. If a build is failing in CI, check the workflow run logs in the [Actions tab](https://github.com/ToCraft/woodwalkers-mod/actions).

---

## Code Style

- The codebase is written in **Java**.
- Follow the conventions already present in the files you're editing (indentation, naming, import ordering).
- Avoid large unrelated refactors in a feature PR — keep the diff readable.

---

## License

Woodwalkers is licensed under the **MIT License**. By submitting a pull request you agree that your contribution will be made available under the same license.



[ModMaster]: https://github.com/ToCraft/ModMaster
[CraftedCore]: https://github.com/ToCraft/craftedcore

# Instant Leaf Decay

Leaves decay instantly when trees are cut. No more floating leaf blobs!

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Loader](https://img.shields.io/badge/loader-Fabric-brightgreen.svg)
![Minecraft](https://img.shields.io/badge/minecraft-26.1-orange.svg)

## Features

- **Instant decay** — leaves disappear the moment they lose connection to a log
- **Chain decay mode** — watch leaves cascade in a satisfying wave from the trunk outwards
- **Extra drops** — configurable bonus chance for saplings, sticks, and apples
- **Sound variety** — multiple break sounds with configurable chance and pitch
- **Particle options** — choose between leaf texture, composter, or villager particles
- **Performance friendly** — uses Minecraft's native tick system, minimal overhead
- **Highly configurable** — 20+ options to tune the mod to your taste
- **Mod compatible** — works with any block extending LeavesBlock
- **Server-side** — clients don't need the mod installed

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/leaf-decay-instant) or [Modrinth](https://modrinth.com/mod/leaf-decay-instant)
4. Drop the `.jar` into your `mods` folder

## Configuration

Config file is created on first launch at `config/instant_leaf_decay.json`.

### Core

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `enabled` | bool | `true` | Master toggle |
| `decayTicks` | int | `2` | Ticks to wait before leaves decay |

### Particles

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `particles` | bool | `true` | Enable particle effects |
| `particleCount` | int | `8` | Number of particles per decay |
| `particleType` | string | `"block"` | `"block"`, `"composter"`, or `"happy"` |

### Sound

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `sound` | bool | `true` | Enable sound effects |
| `soundVolume` | float | `0.8` | Volume (0.0 - 1.0) |
| `soundPitchMin` | float | `1.0` | Minimum pitch |
| `soundPitchMax` | float | `1.2` | Maximum pitch |
| `soundChance` | float | `0.3` | Chance per leaf to play sound |
| `soundEvents` | list | see below | Sound IDs to randomly pick from |

Supported sound IDs: `minecraft:block.grass.break`, `minecraft:block.azalea_leaves.break`, `minecraft:block.moss.break`, `minecraft:block.azalea.break`, `minecraft:block.flowering_azalea.break`, `minecraft:block.cherry_leaves.break`, `minecraft:block.sweet_berry_bush.break`

### Chain Decay

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `chainDecay` | bool | `false` | Enable cascading wave decay |
| `chainDelay` | int | `2` | Spread factor (higher = slower wave) |

### Extra Drops

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `extraSaplingChance` | float | `0.0` | Bonus sapling roll multiplier |
| `extraStickChance` | float | `0.0` | Bonus stick roll multiplier |
| `extraAppleChance` | float | `0.0` | Bonus apple roll (oak/dark oak only) |

### Filters

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `blacklistedLeaves` | list | `[]` | Block IDs to exclude (e.g. `"minecraft:cherry_leaves"`) |
| `disabledDimensions` | list | `[]` | Dimension IDs to disable in |

### Performance

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `requirePlayerNearby` | bool | `false` | Only decay near players |
| `playerRadius` | float | `64.0` | Player detection radius |

## Compatibility

- **Loader:** Fabric
- **Minecraft:** 26.1
- **Requires:** Fabric API
- **Side:** Server-side (clients don't need it)
- **Modded Trees:** 100% compatible out-of-the-box with popular biome mods including **Biomes O' Plenty**, **Terralith**, **Twilight Forest**, and **Nature's Spirit**. (Works automatically with any mod that extends the base `LeavesBlock`).

Support for older versions and Forge/NeoForge coming soon.

## Links

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/leaf-decay-instant)
- [Modrinth](https://modrinth.com/mod/leaf-decay-instant)
- [Issue Tracker](https://github.com/Cukkoo12/leaf-decay-instant/issues)

## License

MIT License — see [LICENSE](LICENSE) for details.

## Credits

Made by **Cukkoo**

## What's New in 2.0

Version 2.0 marks the mod's maturity milestone. Since 1.0, the mod has grown to include:

- Chain decay mode with satisfying cascading waves
- Full in-game command system (`/leafdecay set <option> <value>`)
- Configurable extra drops for saplings, sticks, and apples
- Multiple sound variants with configurable chance
- Particle type selection (block, composter, happy villager)
- Performance options (nearby-player-only mode)
- Blacklists for specific leaves and dimensions
- 20+ configurable options

Future versions will focus on multi-loader support (Forge, NeoForge) and backporting to older Minecraft versions.

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

## Commands

Configuration can also be changed in-game using commands (requires OP level 2):

- `/leafdecay reload` — Reload config from disk
- `/leafdecay set <option> <value>` — Change any option at runtime

**Available options for `set`:**
`enabled`, `particles`, `sound`, `chainDecay`, `requirePlayerNearby`, `decayTicks`, `particleCount`, `chainDelay`, `soundChance`, `soundVolume`, `soundPitchMin`, `soundPitchMax`, `extraSaplingChance`, `extraStickChance`, `extraAppleChance`, `playerRadius`, `particleType`

**Examples:**
```
/leafdecay set chainDecay true
/leafdecay set soundChance 0.5
/leafdecay set particleType composter
/leafdecay reload
```

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
## Commands

The mod provides an in-game command system to change config values at runtime without editing files. All settings can be changed live, and changes are automatically saved to the config file.

> **Note:** Commands are currently available to all players. On multiplayer servers, use a permission mod (e.g. LuckPerms with fabric-permissions-api) to restrict `/leafdecay` to staff.

### Reload config
/leafdecay reload

Reloads the config file from disk. Useful after manual edits.

### Change a setting
/leafdecay reload

Reloads the config file from disk. Useful after manual edits.

### Change a setting
/leafdecay set <option> <value>

**Boolean options** (`true` / `false`):
`enabled`, `particles`, `sound`, `chainDecay`, `requirePlayerNearby`

**Integer options**:
`decayTicks`, `particleCount`, `chainDelay`

**Decimal options**:
`soundChance` (0.0-1.0), `soundVolume` (0.0-1.0), `soundPitchMin`, `soundPitchMax`, `extraSaplingChance`, `extraStickChance`, `extraAppleChance`, `playerRadius`

**String options**:
`particleType` (`block`, `composter`, or `happy`)

### Examples
/leafdecay set chainDecay true
/leafdecay set chainDelay 3
/leafdecay set soundChance 0.5
/leafdecay set particleType composter
/leafdecay set extraSaplingChance 2.0
/leafdecay reload

## Compatibility

- **Loader:** Fabric
- **Minecraft:** 26.1
- **Requires:** Fabric API
- **Side:** Server-side (clients don't need it)
- **Modded Trees:** Should work automatically with any mod that extends the base `LeavesBlock`. Most popular biome mods (Biomes O' Plenty, Terralith, Twilight Forest, Nature's Spirit) do this by default, though compatibility has not been officially tested.

Support for older versions and Forge/NeoForge coming soon.

## Links

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/leaf-decay-instant)
- [Modrinth](https://modrinth.com/mod/leaf-decay-instant)
- [Issue Tracker](https://github.com/Cukkoo12/leaf-decay-instant/issues)

## License

MIT License — see [LICENSE](LICENSE) for details.

## Credits

Made by **Cukkoo**

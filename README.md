# AzoxCmd

A comprehensive utility plugin for Paper 1.21.11 with 70+ commands for teleportation, homes, warps, admin tools, player utilities, inventory management, and more.

## Features

- **70+ Commands** across teleportation, utilities, admin, and inventory categories
- **GUI System** for homes, utilities, vanish settings, teleport menu, and player inspection
- **Home System** with limits, public homes, and interactive management
- **Warp System** with level-based access control
- **Jail System** with timed sentences, inescapable mode, and auto-release
- **Vanish System** with levels, auto-fly, auto-god, fake join/leave messages
- **Inventory Utilities** — portable crafting, anvil, ender chest, grindstone, stonecutter, cartography table, loom, trash
- **Player Buffs** — fly, god mode, heal, feed, speed, night vision
- **Kit System** — create, claim, and cooldown-based kits

## Installation

Drop `AzoxCmd-1.0.jar` into your `plugins/` folder on a Paper 1.21.11 server.

## Commands

| Category | Commands |
| :--- | :--- |
| **Teleportation** | `/tpa`, `/tpahere`, `/tpaccept`, `/tpdecline`, `/tpignore`, `/back`, `/rtp`, `/tpo`, `/tpohere`, `/tpoundo`, `/tp`, `/top`, `/jumpto`, `/world`, `/spawn`, `/lobby` |
| **Homes** | `/sethome`, `/home`, `/delhome`, `/homes`, `/edithome`, `/phome` |
| **Warps** | `/warp`, `/setwarp` |
| **Jail** | `/jail`, `/setjail`, `/deljail`, `/unjail` |
| **Admin** | `/vanish`, `/freeze`, `/see`, `/sudo`, `/tempban`, `/broadcast`, `/remove`, `/break`, `/lightning`, `/burn`, `/extinguish` |
| **Player Utils** | `/fly`, `/god`, `/heal`, `/feed`, `/speed`, `/nv` (night vision), `/ping`, `/stats`, `/tps`, `/uptime`, `/near`, `/getpos`, `/whois`, `/seen`, `/rules`, `/suicide`, `/clearinventory`, `/compass` |
| **Inventory** | `/craft`, `/anvil`, `/enderchest`, `/ec`, `/grindstone`, `/stonecutter`, `/carttable`, `/loom`, `/trash`, `/repair`, `/enchant`, `/itemname`, `/lore`, `/copyitem`, `/condense` |
| **Game** | `/gamemode` (aliases: `/gma`, `/gmc`, `/gms`, `/gmsp`), `/weather`, `/sun`, `/storm`, `/setspawn` |
| **Kits** | `/kit`, `/createkit`, `/delkit` |
| **Settings** | `/config`, `/settings`, `/silence`, `/ilive`, `/fillpot`, `/fillpotsave`, `/permeffect` |
| **Other** | `/azox`, `/azoxreload`, `/near`, `/ext`, `/itemname`, `/lore`, `/renamehome`, `/relocatehome`, `/setphome` |

## GUI Commands

| Command | Description |
| :--- | :--- |
| `/utilities` | Opens the Utility Hub (shows tools you have permission to use). |
| `/config` | Personal settings — toggle GUI menus and particle effects. |
| `/azox` | Admin Configuration — vanish settings and teleport menu. |
| `/see` | Inspect player inventory or enderchest. |

## Permissions

### Default (`azox.cmd.*`)
Granted to all players when no permission manager is detected. Includes:
- Teleport: `/tpa`, `/home`, `/warp`, `/rtp`, `/back`
- Utilities: `/craft`, `/anvil`, `/enderchest`, `/loom`, `/trash`
- Player: `/fly`, `/god`, `/heal`, `/feed`, `/speed`, `/nv`
- Info: `/ping`, `/stats`, `/whois`, `/seen`, `/rules`

### Rank (`azox.cmd.rank.*`)
- `azox.cmd.rank.vip`, `azox.cmd.rank.mod`, `azox.cmd.rank.admin`, `azox.cmd.rank.owner`
- `azox.cmd.particles.*` — visual effects
- `azox.cmd.enderchest.pages.2-5` — additional EC pages
- `azox.cmd.vanish.level.1-5` — vanish visibility levels

### Home Limits
- Default: 4 homes
- Increase: `azox.util.homes.<number>` (e.g., `azox.util.homes.10`)
- Unlimited: `azox.util.sethome.unlimited`

### Kit Permissions
- `azox.util.kit.<name>` — claim specific kit
- `azox.util.kit.*` — all kits
- `azox.util.kit.bypass` — bypass cooldown
- `azox.util.kit.create` — create kits (op)
- `azox.util.kit.delete` — delete kits (op)

### Admin (`azox.cmd.admin.*`)
Default to ops. Includes vanish, jail, freeze, sudo, spawn, lightning, burn, weather, gamemode, and more.

## Storage

Player data stored per-file at `plugins/AzoxCmd/playerdata/<username>_<uuid>.yml`. This keeps settings, homes, and preferences organized and easy to manage.

## Building

```bash
mvn clean package
```

Output: `target/AzoxCmd-1.0.jar`

## Development

| Task | Command |
| :--- | :--- |
| Reload plugin | `/azoxreload` |
| Check server TPS | `/tps` |
| Check uptime | `/uptime` |
| Remove lag sources | `/remove` |

## Configuration

No configuration file required. All settings are player-specific and stored in individual player data files.

## Version

- Requires: Paper 1.21.11
- API: Bukkit (compatible with Paper servers)
# AzoxUtils Documentation

A comprehensive utility plugin for Paper 1.21.11, featuring a modern Home system, Warp management, Teleportation requests, and essential server utilities.

## 🏗️ Storage System
AzoxUtils uses a unified player data storage system. Each player has their own dedicated file located at `plugins/AzoxUtils/playerdata/username_uuid.yml`. This ensures that all settings, homes, and preferences are neatly organized and easy to manage.

## 🖥️ GUI & Admin Systems
AzoxUtils features a comprehensive GUI system for players and admins.

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/utilities` | `/utilities` | `azox.utils.gui` | Opens the Utility Hub (Crafting, Enderchest, etc.). |
| `/azox` | `/azox` | `azox.utils.admin` | Opens the Admin Configuration GUI (Vanish settings, etc.). |
| `/lobby` | `/lobby` | `azox.utils.lobby` | Teleports to the Hub/Lobby world. |

**Features:**
- **Admin Config:** Toggle personal settings for Vanish (Auto Fly, Auto God, Fake Messages, Item Pickup).
- **World Selector:** Automatically given a Compass in the Hub world to navigate servers.
- **Dynamic Utilities:** The `/utilities` menu only shows tools you have permission to use.
- **Ender Chest Pages:** Rank-based access to up to 5 ender chest pages. Access via `/enderchest` or `/ec`.

---

## 🚀 Core Systems

### 🏠 Home System
Manage multiple homes with interactive chat menus and public sharing capabilities.

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/sethome` | `/sethome [name]` | `azox.utils.sethome` | Sets a home at your current location. |
| `/home` | `/home [name]` | `azox.utils.home` | Teleports to a home (3s delay). |
| `/delhome` | `/delhome <name\|all>` | `azox.utils.delhome` | Deletes a home. `/delhome all` requires confirmation. |
| `/homes` | `/homes [page]` | `azox.utils.home` | Lists your homes with interactive hover/click info. |
| `/phome` | `/phome [player:home]` | `azox.utils.phome` | Access public homes of other players. |
| `/edithome` | `/edithome <name>` | `azox.utils.edithome` | Opens an interactive chat menu to manage your home. |

**Home Limits:**
- Default: 4 homes.
- Permission-based: `azox.utils.homes.<number>` (e.g., `azox.utils.homes.10`).
- Unlimited: `azox.utils.sethome.unlimited`.

---

### 🌀 Warp System
Server-wide locations with access levels (1-10).

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/setwarp` | `/setwarp <name> [1-10]` | `azox.utils.setwarp` | Creates a warp with a specific access level. |
| `/warp` | `/warp <name>` | `azox.utils.warp.<level>` | Teleports to a warp if you have the required level. |

---

### ✈️ Teleportation (TPA & TPO)
Modern request system and advanced administrative teleportation.

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/tpa` | `/tpa <player>` | `azox.utils.tpa` | Request to teleport to a player. |
| `/tpahere` | `/tpahere <player>` | `azox.utils.tpahere` | Request a player to teleport to you. |
| `/tpaccept` | `/tpaccept [player]` | `azox.utils.tpa` | Accepts the latest or specific TP request. |
| `/tpdecline` | `/tpdecline` | `azox.utils.tpa` | Declines a pending request. |
| `/tpignore` | `/tpignore` | `azox.utils.tpignore` | Toggle ignoring all incoming requests. |
| `/back` | `/back` | `azox.utils.back` | Return to your last location or death point. |
| `/rtp` | `/rtp` | `azox.utils.rtp` | Randomly teleport within 5000 blocks. |
| `/tpo` | `/tpo <player>` | `azox.utils.tpo` | Teleport to an online or offline player. |
| `/tpohere` | `/tpohere <player>` | `azox.utils.tpo` | Teleport an online or offline player to you. |
| `/tpoundo` | `/tpoundo <player>` | `azox.utils.tpo` | Undo the last `/tpohere` operation. |

---

### 🛡️ Admin & Miscellaneous

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/remove` | `/remove <type> [radius]` | `azox.utils.remove` | Remove entities (items, mobs, etc.) to reduce lag. |
| `/createkit` | `/createkit <name> [cooldown]` | `azox.utils.createkit` | Create a kit from your current inventory. |
| `/kit` | `/kit <name>` | `azox.utils.kit` | Claim a kit. |
| `/delkit` | `/delkit <name>` | `azox.utils.delkit` | Delete a kit. |
| `/vanish` | `/vanish [gui\|tipu\|fakejoin\|fakeleave]` | `azox.utils.vanish` | Advanced vanish system with stealth features. |
| `/jail` | `/jail <player> <jailname> [escapable\|not] [dramatic]` | `azox.utils.jail` | Jail system with dramatic effects. |
| `/freeze` | `/freeze <player>` | `azox.utils.freeze` | Prevent a player from moving or interacting. |

## 👑 Rank System (Permissions)
AzoxUtils integrates with permissions to provide rank-based benefits:
- **Prefixes:** `azox.utils.rank.<name>` (e.g., owner, admin, mod, vip).
- **Particles:** `azox.utils.particles.<effect>`.
- **Feed Cooldown:** `azox.utils.feed.cooldown.<seconds>` (e.g., 3600 for 1 hour).
- **Ender Chest Pages:** `azox.utils.enderchest.pages.<1-5>`.
- **Vanish Levels:** `azox.utils.vanish.level.<number>`.

## 🎨 Design Features
- **MiniMessage Support:** All messages use modern `<color>` tags and support hover/click events.
- **Geyser Compatible:** Avoids complex GUI containers where possible, using interactive chat components that work perfectly for Bedrock players.
- **Smart Completion:** Tab-completion for homes, warps, players, and coordinates, respecting vanish levels.

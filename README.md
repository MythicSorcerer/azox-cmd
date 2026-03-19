# AzoxUtils Documentation

A comprehensive utility plugin for Paper 1.21.1, featuring a modern Home system, Warp management, Teleportation requests, and essential server utilities.

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

### ✈️ Teleportation (TPA)
Modern request system with interactive accept/deny buttons.

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/tpa` | `/tpa <player>` | `azox.utils.tpa` | Request to teleport to a player. |
| `/tpahere` | `/tpahere <player>` | `azox.utils.tpahere` | Request a player to teleport to you. |
| `/tpaccept` | `/tpaccept [player]` | `azox.utils.tpa` | Accepts the latest or specific TP request. |
| `/tpdecline` | `/tpdecline` | `azox.utils.tpa` | Declines a pending request. |
| `/tpignore` | `/tpignore` | `azox.utils.tpignore` | Toggle ignoring all incoming requests. |
| `/back` | `/back` | `azox.utils.back` | Return to your last location or death point. |
| `/rtp` | `/rtp` | `azox.utils.rtp` | Randomly teleport within 5000 blocks. |

---

### 🛠️ Utilities

#### 📦 Inventory
- `/enderchest` (`/ec`): `azox.utils.enderchest` - Open your enderchest.
- `/anvil`: `azox.utils.anvil` - Open a portable anvil.
- `/carttable`: `azox.utils.cartographytable` - Open a cartography table.
- `/trash`: `azox.utils.trash` - Open a disposal inventory.
- `/condense` (`/compact`): `azox.utils.condense` - Turn ingots into blocks in your inventory.
- `/clear`: `azox.utils.clearinventory` - Clear your inventory.

#### 👤 Player
- `/feed`: `azox.utils.feed` - Fill hunger and saturation.
- `/heal`: `azox.utils.heal` - Restore health and remove negative effects.
- `/fly`: `azox.utils.fly` - Toggle flight mode.
- `/god`: `azox.utils.god` - Toggle invulnerability.
- `/speed <1-10>`: `azox.utils.speed` - Set walk or fly speed.

#### 🎮 Gamemode
- `/gm <s\|c\|a\|sp>`: `azox.utils.gamemode` - Switch gamemodes.
- `/gms`, `/gmc`, `/gma`, `/gmsp`: Permission shortcuts for specific modes.

---

### 🛡️ Admin & Miscellaneous

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/tp` | `/tp <args...>` | `azox.utils.tp` | Powerful teleport (Players, Coords, Worlds). |
| `/sudo` | `/sudo <player> <cmd>` | `azox.utils.sudo` | Force a player to run a command. |
| `/whois` | `/whois <player>` | `azox.utils.whois` | View player UUID, status, and last seen. |
| `/broadcast` | `/bc <message>` | `azox.utils.broadcast` | Send a server-wide announcement. |
| `/setspawn` | `/setspawn` | `azox.utils.setspawn` | Set the world spawn point. |
| `/lightning` | `/lightning [player]` | `azox.utils.lightning` | Strike a player with lightning. |
| `/burn` | `/burn <player> [sec]` | `azox.utils.burn` | Set a player on fire. |
| `/break` | `/break` | `azox.utils.break` | Break the block you are looking at. |

## 🎨 Design Features
- **MiniMessage Support:** All messages use modern `<color>` tags and support hover/click events.
- **Geyser Compatible:** Avoids complex GUI containers where possible, using interactive chat components that work perfectly for Bedrock players.
- **Smart Completion:** Tab-completion for homes, warps, players, and coordinates.

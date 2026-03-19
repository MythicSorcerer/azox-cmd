# Project Plan: azox-utils

## 1. Project Initialization
- [ ] Initialize Maven project with Paper API 1.21.1.
- [ ] Add dependencies: Lombok, MiniMessage (Kyori), and any necessary libraries for GUI (if not using standard inventories).
- [ ] Create the main plugin class `AzoxUtils` with a static instance.
- [ ] Setup `plugin.yml` with basic metadata.

## 2. Core Systems
- [ ] **Configuration System:** Handle messages, home limits, teleport cooldowns, and warp settings.
- [ ] **Data Management:** Implement storage for homes, warps, jails, and kits (YAML or SQLite).
- [ ] **Message Utility:** MiniMessage wrapper for consistent coloring and icons.

## 3. Home System
- [ ] `/sethome`: Handle home limits (hook into `azox-ranks`).
- [ ] `/home`: Teleport with cooldown and movement check.
- [ ] `/delhome`: Confirmation logic.
- [ ] `/edithome` & `/homes`: Interactive chat menus/GUI for management.
- [ ] `/phome`: Public home system.

## 4. Teleportation & Warp Systems
- [ ] `/tpa`, `/tpahere`, `/tpaccept`, `/tpdecline`, `/tpignore`: Request management.
- [ ] `/warp`, `/setwarp`: Level-based warp access.
- [ ] `/rtp`: Random teleportation logic.
- [ ] `/back`: Track last location and death location.

## 5. Utility & Admin Commands
- [ ] **Inventory Utilities:** `/enderchest`, `/anvil`, `/cartographytable`, `/trash`, `/clearinventory`.
- [ ] **Player Management:** `/whois`, `/seen`, `/jail`, `/god`, `/heal`, `/feed`, `/fly`, `/speed`, `/gamemode`, `/sudo`, `/tempban`.
- [ ] **World/Item Utilities:** `/tp` (various), `/setspawn`, `/weather`, `/sun`, `/storm`, `/enchant`, `/lore`, `/condense`, `/break`, `/lightning`.
- [ ] **Misc:** `/broadcast`, `/rules`, `/suicide`, `/getpos`, `/compass`.

## 6. Integration & Validation
- [ ] Hook into `azox-ranks` for permission-based limits.
- [ ] Ensure Geyser compatibility (avoid interactive elements that break on bedrock).
- [ ] Final testing of all commands and permissions.

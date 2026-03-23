# Development Plan

## 🧭 Teleport Menu System
- [ ] Create teleport menu with the following structure:
  - [ ] List of all dimensions first
  - [ ] List of online players (playerheads, click to teleport)
  - [ ] Offline players (heads, also click to teleport)
  - [ ] Next page navigation

## 🛡️ Admin Config Menu
- [ ] Clean up azox admin config to only contain:
  - [ ] Vanish settings
  - [ ] Teleport menu access
- [ ] Redesign admin config menu layout:
  - [ ] Remove concrete blocks from menu
  - [ ] Layout: `--V---T--` (V = vanish settings, T = teleport)

## 🐛 Bug Fixes
- [ ] Fix `/v tipu` not working (item pickup toggle in vanish config menu works, but command doesnERe)

## ⚙️ Configuration System
- [ ] Move GUI toggle to `/config` menu where more configurables can be added later

## 🌙 Night Vision System
- [ ] Add `/nv` command with aliases: `/nvt`, `/nightvision`, `/nightvisiontoggle`
- [ ] Command runs: `/effect give playername night_vision infinite 0 true`
- [ ] Re-apply effect on player respawn
- [ ] Store preference in config so it persists after re-login

## 🔧 Utilities
- [ ] Add loom to utilities menu

## 👑 Permission System
- [ ] Move permission nodes for player-use commands to `azox.user.*`:
  - [ ] `tpa`, `tpaccept`, `home`, `sethome`, etc.
- [ ] Move advanced permissions to `azox.rank.*`:
  - [ ] `enderchest`, `crafting table`, etc.
- [ ] Move admin-only commands to `azox.admin.*`
- [ ] If no permission manager plugin, allow all players all permissions under `azox.user.*`
- [ ] Grant all permissions to ops
- [ ] Ranks can be implemented later

## 📝 Documentation
- [ ] Update `README.md` with new features

# Development Plan

## 1. Storage & Configuration
- [ ] Update `PlayerStorage` to handle per-admin preferences:
    - [ ] `vanish_fake_messages` (default: true)
    - [ ] `vanish_auto_fly` (default: true)
    - [ ] `vanish_auto_god` (default: true)
    - [ ] `vanish_no_pickup` (default: true)
- [ ] Update `PlayerStorage` for God Mode mob targeting preference (global or per player? User said "configurable if mobs target player", implies setting).

## 2. Command Refactoring & Updates (`plugin.yml`)
- [ ] Rename `/azoxgui` to `/utilities`.
- [ ] Add `/azox` or `/azoxadmin` command for the new Admin GUI.
- [ ] Add aliases for God Mode: `/invuln`, `/invulnerable`, `/godmode`.
- [ ] Add `/lobby` command.
- [ ] Ensure `/fly` works in survival (verify logic).

## 3. Vanish System Overhaul
- [ ] Update `VanishManager` to use `PlayerStorage` preferences.
- [ ] Implement Fake Join/Leave logic on vanish toggle based on prefs.
- [ ] Implement Auto Fly/God logic on vanish toggle.
- [ ] Create `/vanish gui` to toggle these settings (using Concrete blocks for On/Off visualization).

## 4. God Mode Enhancements
- [ ] Update `PlayerListener` to cancel `EntityTargetEvent` if target is in God Mode.

## 5. GUI Systems
- [ ] **Utilities GUI:** Update to only show items the player has permission for.
- [ ] **Admin Configuration GUI:** New GUI to manage personal admin settings (Vanish prefs, GUI mode toggle).
- [ ] **Homes GUI:** Ensure it visualizes correctly (user mentioned concrete/glass panes, likely for status indicators or styling).

## 6. Lobby & World System
- [ ] Implement `/lobby` command (teleport to "world", "lobby", or "hub").
- [ ] **Compass Item:**
    - [ ] Give on Join/WorldChange if in a lobby world.
    - [ ] Right-click opens World Selector GUI.
- [ ] **World Selector GUI:**
    - [ ] Survival (Group `world`, `nether`, `end`).
    - [ ] Lobby.
    - [ ] Dynamic list of other worlds.

## 7. Final Polish
- [ ] Update `README.md`.
- [ ] Verify compilation (`mvn package`).

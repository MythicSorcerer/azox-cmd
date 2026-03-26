# Development TODO

## ✅ Completed

### Core Fixes
- [x] Fix plugin initialization crash (moved manager initialization to `onEnable()`)
- [x] Update version to 1.0.0 (removed SNAPSHOT)
- [x] Fix concrete tooltips (status indicators now show "✔ Enabled" / "✘ Disabled")

### Jail System Improvements
- [x] Update jail messages to "sentenced to solitary confinement"
- [x] Add global broadcast when player is jailed
- [x] Add admin-only messages for jail release/escape
- [x] Add time component to `/jail` command (e.g., `1d12h30m`)
- [x] Add inescapable jail effects: Blindness I, Slowness XXV, Mining Fatigue XXV
- [x] Auto-teleport back if player leaves inescapable jail
- [x] Auto-release when jail time expires

### Bug Fixes
- [x] Fix Vanish Settings back button (returns to Admin menu)
- [x] Remove vanish settings from `/config` menu (admin only via `/azox`)

### Permissions
- [x] Auto-grant `azox.user.*` if no permission manager detected
- [x] Supports: LuckPerms, PermissionsEx, GroupManager, UltraPermissions, zPermissions

### Code Quality
- [x] Add null safety throughout
- [x] Use `final` modifiers
- [x] Use static `AzoxUtils.getInstance()` pattern
- [x] Use fully qualified variable names

---

## 📋 Remaining Tasks

### Documentation
- [ ] Update `README.md` with new features

### Future Enhancements
- [ ] Investigate plugin hot-reloading capability
- [ ] Add more configuration options to `/config` menu

---

## 📝 Notes
- Keep this file updated as tasks are completed
- Use checkboxes `[ ]` for pending, `[x]` for completed

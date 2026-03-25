# Development TODO

## ✅ Completed

### Jail System Improvements
- [x] Update jail messages:
  - [x] Change dramatic jail message to "You have been sentenced to solitary confinement"
  - [x] Make "Player has been jailed/solitary confinement" message global
  - [x] Add escape/release message visible only to admins and in logs
- [x] Add time component to `/jail` command:
  - [x] Default: forever ("indefinitely")
  - [x] Timed: "28d12h32m1s" → "You will be released in 28.5 days"
  - [x] Supports formats: `1d`, `12h`, `30m`, `45s` or any combination
- [x] Inescapable jail enhancements:
  - [x] Apply Blindness I (infinite) while in jail
  - [x] Apply Slowness XXV (infinite) while in jail
  - [x] Apply Mining Fatigue XXV (infinite) while in jail
  - [x] Teleport back if they leave the jail area
  - [x] Auto-release when time expires

### Bug Fixes
- [x] Fix Vanish Settings back button (now returns to Admin menu)
- [x] Move vanish config out of `/config` menu (accessible only via Admin menu)

### Permissions
- [x] If no permission manager plugin, grant all players `azox.user.*` by default
- [x] Detects: LuckPerms, PermissionsEx, GroupManager, UltraPermissions, zPermissions

### Code Quality
- [x] Add null safety throughout
- [x] Use `final` modifiers
- [x] Use static `AzoxUtils.getInstance()` pattern
- [x] Use fully qualified variable names

---

## 📋 Remaining Tasks

### Documentation
- [ ] Update `README.md` with new features

---

## 📝 Notes
- Keep this file updated as tasks are completed
- Use checkboxes `[ ]` for pending, `[x]` for completed

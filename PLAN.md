# Project Plan: azox-cmd Refactor

## 1. Baseline Verification
- [ ] Compile to ensure a clean starting point.
- [ ] Note any build warnings that may impact refactor changes.

## 2. Style & Safety Refactor
- [ ] Align kit command/storage/manager classes with naming and null-safety standards.
- [ ] Use Lombok getters/setters where models expose state.
- [ ] Standardize `this` usage for field access and add `final` where possible.
- [ ] Ensure plugin instance access uses `AzoxCmd.getInstance()`.

## 3. Permissions & Config Alignment
- [ ] Ensure kit permissions are declared and used consistently.
- [ ] Keep Paper API version at 1.21.11.

## 4. Verification & Commit
- [ ] Compile after refactors to confirm no regressions.
- [ ] Commit changes with a concise message.

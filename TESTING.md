# Testing AzoxCmd Plugin

## Permission Restriction Testing

To verify that commands are properly restricted to administrators:

### Test Setup
1. Start a PaperMC 1.21.11 server
2. Install the AzoxCmd plugin
3. Create test players:
   - Admin player with "azoxcmd.admin" permission
   - OP player (without explicit permission)
   - Regular player (no special permissions or OP status)

### Test Cases for Each Command (/fly, /flyspeed, /top, /anvil, /craft, /wb, etc.)

#### Test 1: Regular Player (No Permission)
- Expected: Command fails with "You do not have permission to use this command."
- Actual: [Record result]

#### Test 2: Player with azoxcmd.admin Permission
- Expected: Command executes successfully
- Actual: [Record result]

#### Test 3: OP Player (No explicit permission)
- Expected: Command executes successfully (fallback to OP check)
- Actual: [Record result]

#### Test 4: Console/Sender
- Expected: Command fails with "This command can only be executed by a player."
- Actual: [Record result]

### Specific Command Tests

#### /flyspeed Additional Tests
- Invalid number: Should show error "Speed must be a number."
- Out of range (<0.0 or >1.0): Should show error "Speed must be between 0.0 and 1.0."
- Valid number: Should set fly speed and confirm

#### /top Additional Tests
- Should teleport to highest solid block
- If no solid block found: Should show error "Could not find a solid block below."

### Automation Note
For automated testing, consider using a testing framework like Mockito to mock Bukkit objects and verify command execution logic without needing a live server.

# AzoxCmd Plugin Development Plan

## Objective
Create a Minecraft plugin (AzoxCmd) that provides various commands (/fly, /flyspeed, /top, /anvil, /craft, /wb, etc.) but restricts them to administrators only (by default) using a permission check with a fallback to OP status.

## Steps
1. Set up the project structure with Maven.
2. Create the main plugin class (AzoxCmd.java) that extends JavaPlugin.
3. Implement the onEnable() method to register commands and load configuration.
4. Create command executor classes for each command (FlyCommand, FlySpeedCommand, TopCommand, AnvilCommand, CraftCommand, WbCommand) that check for administrator permissions.
5. Implement the permission check: 
   - Primary: Check if the player has the permission "azoxcmd.admin".
   - Fallback: If the above fails, check if the player is OP (player.isOp()).
6. If the player does not have the required permission, send a message indicating insufficient permissions and return false (to prevent command execution).
7. Implement the actual functionality for each command (to be filled in later).
8. Ensure the plugin follows the coding standards: use Lombok, fully qualified variable names, null checks, etc.
9. Store a static instance of the plugin in the main class for easy access.
10. Test the plugin to ensure commands are restricted correctly.

## Command Details
- /fly: Toggles flight for the player.
- /flyspeed: Sets the player's flight speed.
- /top: Teleports the player to the highest block at their current location.
- /anvil: Opens an anvil inventory for the player.
- /craft: Opens a crafting inventory for the player.
- /wb: Possibly stands for "world builder" or gives a world edit-like tool? We'll clarify later.

Note: The actual implementation of each command's functionality is beyond the scope of this permission restriction task, but we will stub them out.

## Files to Create
- src/main/java/com/azox/cmd/AzoxCmd.java
- src/main/java/com/azox/cmd/command/FlyCommand.java
- src/main/java/com/azox/cmd/command/FlySpeedCommand.java
- src/main/java/com/azox/cmd/command/TopCommand.java
- src/main/java/com/azox/cmd/command/AnvilCommand.java
- src/main/java/com/azox/cmd/command/CraftCommand.java
- src/main/java/com/azox/cmd/command/WbCommand.java
- src/main/resources/plugin.yml
- src/main/resources/config.yml (optional, for configuration)

## Coding Standards
- Use Lombok for getters, setters, and constructors.
- Use fully qualified variable names (e.g., player instead of p).
- Use "this" consistently.
- Add "final" where possible.
- Check for errors and handle null safety.
- Store a static plugin instance in the main class.

## Testing
- Verify that non-admin players cannot execute the restricted commands.
- Verify that admin players (by permission or OP) can execute the commands.
- Ensure appropriate permission denial messages are sent.
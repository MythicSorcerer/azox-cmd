package com.azox.cmd.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.azox.cmd.AzoxCmd;

/**
 * Command executor for /flyspeed command.
 */
public class FlySpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        // Check permission: azoxcmd.admin or OP
        if (!hasPermission(player)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Check arguments
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /flyspeed <speed>");
            return true;
        }

        // Parse speed
        float speed;
        try {
            speed = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Speed must be a number.");
            return true;
        }

        // Validate speed (0.0f to 1.0f)
        if (speed < 0.0f || speed > 1.0f) {
            player.sendMessage(ChatColor.RED + "Speed must be between 0.0 and 1.0.");
            return true;
        }

        // Set fly speed
        player.setFlySpeed(speed);
        player.sendMessage(ChatColor.GREEN + "Fly speed set to " + speed + ".");

        return true;
    }

    /**
     * Checks if the player has the required permission (azoxcmd.admin) or is OP.
     *
     * @param player the player to check
     * @return true if the player has permission or is OP, false otherwise
     */
    private boolean hasPermission(Player player) {
        // Check for the specific permission
        if (player.hasPermission("azoxcmd.admin")) {
            return true;
        }
        // Fallback to OP status
        return player.isOp();
    }
}
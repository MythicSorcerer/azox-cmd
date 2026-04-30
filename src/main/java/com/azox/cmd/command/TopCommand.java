package com.azox.cmd.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.azox.cmd.AzoxCmd;

/**
 * Command executor for /top command.
 */
public class TopCommand implements CommandExecutor {

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

        // Get the highest block at the player's current location
        int x = player.getLocation().getBlockX();
        int z = player.getLocation().getBlockZ();
        int worldHeight = player.getWorld().getMaxHeight();
        int y = worldHeight - 1;

        // Find the highest non-air block
        while (y > 0) {
            if (!player.getWorld().getBlockAt(x, y, z).isAir()) {
                break;
            }
            y--;
        }

        // If we found a block, teleport the player to just above it
        if (y > 0) {
            player.teleport(player.getWorld().getLocation(x, y + 1, z));
            player.sendMessage(ChatColor.GREEN + "Teleported to the top.");
        } else {
            player.sendMessage(ChatColor.RED + "Could not find a solid block below.");
        }

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
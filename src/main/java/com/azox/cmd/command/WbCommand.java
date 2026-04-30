package com.azox.cmd.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.azox.cmd.AzoxCmd;

/**
 * Command executor for /wb command (World Builder or similar).
 * This is a stub implementation that can be expanded later.
 */
public class WbCommand implements CommandExecutor {

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

        // Stub implementation: just send a message
        player.sendMessage(ChatColor.GREEN + "World Builder command executed (stub).");
        // TODO: Implement actual world builder functionality

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
package net.azox.cmd.command.impl.util;

import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class MiscUtilCommands extends BaseCommand {

    private final String type;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public MiscUtilCommands(final String type) {
        this.type = type;
    }

    @Override
    public void execute(final CommandSender sender, final String label, final String[] args) {
        if (sender == null || args == null) {
            return;
        }
        if (this.type == null) {
            MessageUtil.sendMessage(sender, "<red>Command type missing.");
            return;
        }
        switch (this.type.toLowerCase()) {
            case "getpos":
                if (!isPlayer(sender) && args.length == 0) {
                    return;
                }
                final Player targetPlayer = args.length > 0 ? Bukkit.getPlayer(args[0]) : (Player) sender;
                if (targetPlayer == null) {
                    MessageUtil.sendMessage(sender, "<red>Player not found!");
                    return;
                }
                final Location location = targetPlayer.getLocation();
                final World world = location.getWorld();
                if (world == null) {
                    MessageUtil.sendMessage(sender, "<red>World not found for player!");
                    return;
                }
                MessageUtil.sendMessage(sender, "<gold>" + targetPlayer.getName() + "'s Coordinates: <yellow>X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ() + " (" + world.getName() + ")");
                break;
            case "whois":
                if (args.length == 0) {
                    MessageUtil.sendMessage(sender, "<red>Usage: /whois <player>");
                    return;
                }
                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                MessageUtil.sendMessage(sender, "<gold>Whois: <yellow>" + offlinePlayer.getName());
                MessageUtil.sendMessage(sender, "<gray>UUID: " + offlinePlayer.getUniqueId());
                MessageUtil.sendMessage(sender, "<gray>Online: " + (offlinePlayer.isOnline() ? "<green>Yes" : "<red>No"));
                if (offlinePlayer.getLastPlayed() > 0) {
                    MessageUtil.sendMessage(sender, "<gray>Last Seen: " + DATE_FORMAT.format(new Date(offlinePlayer.getLastPlayed())));
                }
                break;
            case "broadcast":
                if (args.length == 0) {
                    MessageUtil.sendMessage(sender, "<red>Usage: /broadcast <message>");
                    return;
                }
                final String message = String.join(" ", args);
                Bukkit.broadcast(MessageUtil.parse("<red><bold>[Broadcast]</bold> <white>" + message));
                break;
            case "suicide":
                if (!isPlayer(sender)) {
                    return;
                }
                ((Player) sender).setHealth(0);
                MessageUtil.sendMessage(sender, "<red>You took your own life.");
                break;
            case "break":
                if (!isPlayer(sender)) {
                    return;
                }
                final Player player = (Player) sender;
                final Block block = player.getTargetBlockExact(5);
                if (block == null || block.getType() == Material.AIR) {
                    MessageUtil.sendMessage(player, "<red>No block in range!");
                    return;
                }
                block.setType(Material.AIR);
                MessageUtil.sendMessage(player, "<green>Block broken!");
                break;
            case "compass":
                if (!isPlayer(sender)) {
                    return;
                }
                final Player compassPlayer = (Player) sender;
                float yaw = compassPlayer.getLocation().getYaw();
                String direction = "North";
                if (yaw < 0) {
                    yaw += 360;
                }
                if (yaw >= 315 || yaw < 45) {
                    direction = "South";
                } else if (yaw >= 45 && yaw < 135) {
                    direction = "West";
                } else if (yaw >= 135 && yaw < 225) {
                    direction = "North";
                } else if (yaw >= 225 && yaw < 315) {
                    direction = "East";
                }
                MessageUtil.sendMessage(compassPlayer, "<gold>You are facing: <yellow>" + direction);
                break;
            default:
                MessageUtil.sendMessage(sender, "<red>Unknown command type.");
                break;
        }
    }

    @Override
    public List<String> complete(final CommandSender sender, final String[] args) {
        if (args == null) {
            return new ArrayList<>();
        }
        if (args.length == 1) {
            if (this.type.equalsIgnoreCase("getpos") || this.type.equalsIgnoreCase("whois")) {
                return this.getVisiblePlayerNames(sender, args[0]);
            }
        }
        return new ArrayList<>();
    }
}

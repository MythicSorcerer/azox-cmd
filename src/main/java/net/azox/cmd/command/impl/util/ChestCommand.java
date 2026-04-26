package net.azox.cmd.command.impl.util;

import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.manager.LockChestManager;
import net.azox.cmd.model.ChestLock;
import net.azox.cmd.model.ChestLog;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class ChestCommand extends BaseCommand {

    private final LockChestManager lockChestManager = this.plugin.getLockChestManager();

    @Override
    public void execute(final CommandSender sender, final String label, final String[] args) {
        if (this.permissionDenied(sender, "azox.util.lockchest")) {
            return;
        }
        if (!this.isPlayer(sender)) {
            final var targetPlayer = args.length > 0 ? this.plugin.getServer().getPlayer(args[0]) : null;
            if (targetPlayer == null) {
                MessageUtil.sendMessage(sender, "<red>Player not found!");
                return;
            }
            if (this.permissionDenied(sender, "azox.util.lockchest.others")) {
                return;
            }
            this.showChestInfo(sender, targetPlayer.getUniqueId().toString());
            return;
        }

        final Player player = (Player) sender;
        final Block block = player.getTargetBlockExact(5);

        if (block == null || !(block.getState() instanceof Chest)) {
            MessageUtil.sendMessage(player, "<red>You are not looking at a chest!");
            return;
        }

        final ChestLock chest = this.lockChestManager.getChestAt(block.getLocation());
        if (chest == null) {
            MessageUtil.sendMessage(player, "<gray>This chest is not locked.");
            return;
        }

        this.showChestInfo(sender, chest);

        if (chest.getOwnerUuid().equals(player.getUniqueId()) || player.hasPermission("azox.util.lockchest.others")) {
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, "<gold>Recent Activity:");
            final List<ChestLog> logs = this.lockChestManager.getChestLogs(chest.getChestId(), 5);
            if (logs.isEmpty()) {
                MessageUtil.sendMessage(sender, "<gray>No recent activity.");
            } else {
                for (final ChestLog log : logs) {
                    final String timeAgo = this.formatTimeAgo(log.getTimestamp());
                    MessageUtil.sendMessage(sender, "<gray>" + log.getAction().name() + " - " + timeAgo);
                }
            }
        }
    }

    private void showChestInfo(final CommandSender sender, final String chestId) {
        final ChestLock chest = this.lockChestManager.getChestById(chestId);
        if (chest == null) {
            MessageUtil.sendMessage(sender, "<red>Chest not found!");
            return;
        }
        this.showChestInfo(sender, chest);
    }

    private void showChestInfo(final CommandSender sender, final ChestLock chest) {
        final var ownerName = this.getOwnerName(chest.getOwnerUuid());

        MessageUtil.sendMessage(sender, "<gold>=== Chest Info ===");
        MessageUtil.sendMessage(sender, "<gray>ID: <white>" + chest.getChestId());
        MessageUtil.sendMessage(sender, "<gray>Owner: <white>" + ownerName);
        MessageUtil.sendMessage(sender, "<gray>Location: <white>" + chest.getWorldName() + " " + chest.getX() + "," + chest.getY() + "," + chest.getZ());
        MessageUtil.sendMessage(sender, "<gray>Allowed Players: <white>" + 
            (chest.getAllowedPlayers() != null ? chest.getAllowedPlayers().size() : 0));

        if (chest.getAllowedPlayers() != null && !chest.getAllowedPlayers().isEmpty()) {
            final var allowedNames = new java.util.ArrayList<String>();
            for (final var uuid : chest.getAllowedPlayers()) {
                allowedNames.add(this.getOwnerName(uuid));
            }
            if (!allowedNames.isEmpty()) {
                MessageUtil.sendMessage(sender, "<gray>  > <white>" + String.join(", ", allowedNames));
            }
        }
    }

    private String getOwnerName(final java.util.UUID uuid) {
        final var player = this.plugin.getServer().getPlayer(uuid);
        if (player != null) {
            return player.getName();
        }
        final var offlinePlayer = this.plugin.getServer().getOfflinePlayer(uuid);
        return offlinePlayer != null ? offlinePlayer.getName() : "Unknown";
    }

    private String formatTimeAgo(final long timestamp) {
        final long diff = System.currentTimeMillis() - timestamp;
        if (diff < 60000) {
            return "< 1 minute ago";
        } else if (diff < 3600000) {
            return "< " + (diff / 60000) + " minutes ago";
        } else if (diff < 86400000) {
            return "< " + (diff / 3600000) + " hours ago";
        } else {
            return "< " + (diff / 86400000) + " days ago";
        }
    }

    @Override
    public List<String> complete(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            return new java.util.ArrayList<>();
        }
        if (args.length == 1) {
            return this.getVisiblePlayerNames(sender, args[0]);
        }
        return new java.util.ArrayList<>();
    }
}
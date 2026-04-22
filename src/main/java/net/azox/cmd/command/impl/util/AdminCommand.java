package net.azox.cmd.command.impl.util;

import net.azox.cmd.AzoxCmd;
import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AdminCommand extends BaseCommand {

    @Override
    public void execute(final CommandSender sender, final String label, final String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("azox.util.admin.reload")) {
                MessageUtil.sendMessage(sender, "<red>" + MessageUtil.ICON_ERROR + " You don't have permission to reload!");
                return;
            }
            reloadConfig(sender);
            return;
        }

        if (!isPlayer(sender)) {
            MessageUtil.sendMessage(sender, "<red>This command must be executed by a player.");
            return;
        }
        this.plugin.getGuiManager().openAdminGui((Player) sender);
    }

    private void reloadConfig(final CommandSender sender) {
        MessageUtil.sendMessage(sender, "<yellow>" + MessageUtil.ICON_INFO + " Reloading AzoxCmd configuration...");

        try {
            // Reload warp data
            this.plugin.getWarpManager().getStorage().reload();

            // Reload jail data
            this.plugin.getJailManager().getStorage().reload();

            // Reload kit data
            this.plugin.getKitManager().getStorage().reload();

            // Clear and reload home cache for all online players
            for (final Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (player != null) {
                    final java.util.Map<String, net.azox.cmd.model.Home> homes = this.plugin.getHomeManager().getHomes(player);
                    homes.clear();
                    homes.putAll(this.plugin.getPlayerStorage().getHomes(player));
                }
            }

            MessageUtil.sendMessage(sender, "<green>" + MessageUtil.ICON_SUCCESS + " Configuration reloaded successfully!");
            MessageUtil.sendMessage(sender, "<gray>Note: Some changes may require a server restart.");
        } catch (final Exception exception) {
            MessageUtil.sendMessage(sender, "<red>" + MessageUtil.ICON_ERROR + " Failed to reload configuration!");
            AzoxCmd.getInstance().getLogger().warning("Failed to reload config: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}

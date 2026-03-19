package com.azox.utils.command.impl.util;

import com.azox.utils.command.BaseCommand;
import com.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class GuiCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length == 0) {
            plugin.getGuiManager().openUtilitiesGui(player);
            return;
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            final boolean current = plugin.getTeleportManager().getStorage().isGuiEnabled(player.getUniqueId());
            final boolean next = !current;
            plugin.getTeleportManager().getStorage().setGuiEnabled(player.getUniqueId(), next);
            MessageUtil.sendMessage(player, "<green>GUI menus are now " + (next ? "<green>enabled" : "<red>disabled") + "!");
        } else {
            MessageUtil.sendMessage(player, "<red>Usage: /azoxgui [toggle]");
        }
    }
}

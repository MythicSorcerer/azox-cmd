package net.azox.cmd.command.impl.util;

import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            final boolean current = plugin.getPlayerStorage().isGuiEnabled(player);
            final boolean next = !current;
            plugin.getPlayerStorage().setGuiEnabled(player, next);
            MessageUtil.sendMessage(player, "<green>GUI menus are now " + (next ? "<green>enabled" : "<red>disabled") + "!");
        } else {
            MessageUtil.sendMessage(player, "<red>Usage: /azoxgui [toggle]");
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("toggle");
        }
        return new ArrayList<>();
    }
}

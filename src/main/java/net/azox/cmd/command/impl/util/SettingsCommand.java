package net.azox.cmd.command.impl.util;

import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SettingsCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("gui")) {
            boolean current = this.plugin.getPlayerStorage().isGuiEnabled(player);
            this.plugin.getPlayerStorage().setGuiEnabled(player, !current);
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " GUI menus are now " + (!current ? "<green>enabled" : "<red>disabled") + "!");
            return;
        }

        if (args.length > 0 && (args[0].equalsIgnoreCase("particles") || args[0].equalsIgnoreCase("particle"))) {
            boolean current = this.plugin.getPlayerStorage().areParticlesEnabled(player);
            this.plugin.getPlayerStorage().setParticlesEnabled(player, !current);
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Particles are now " + (!current ? "<green>enabled" : "<red>disabled") + "!");
            return;
        }

        this.plugin.getGuiManager().openConfigGui(player);
    }
}

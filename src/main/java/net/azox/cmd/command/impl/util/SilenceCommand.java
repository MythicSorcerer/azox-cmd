package net.azox.cmd.command.impl.util;

import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class SilenceCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        boolean currentState = this.plugin.getPlayerStorage().isSilenced(player);
        boolean newState = !currentState;

        this.plugin.getPlayerStorage().setSilenced(player, newState);

        if (newState) {
            MessageUtil.sendMessage(player, "<yellow>" + MessageUtil.ICON_INFO + " Silence enabled. Command responses are now hidden.");
        } else {
            MessageUtil.sendMessage(player, "<yellow>" + MessageUtil.ICON_INFO + " Silence disabled. Command responses are now visible.");
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}

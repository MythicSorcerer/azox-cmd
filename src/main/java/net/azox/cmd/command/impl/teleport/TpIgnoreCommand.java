package net.azox.cmd.command.impl.teleport;

import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TpIgnoreCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        final boolean current = this.plugin.getPlayerStorage().isTpIgnore(player);
        final boolean next = !current;
        
        this.plugin.getPlayerStorage().setTpIgnore(player, next);
        MessageUtil.sendMessage(player, "<green>Teleport requests are now " + (next ? "<red>ignored" : "<green>accepted") + "!");
    }
}

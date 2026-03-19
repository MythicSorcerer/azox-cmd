package com.azox.utils.command.impl.teleport;

import com.azox.utils.command.BaseCommand;
import com.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TpIgnoreCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        final boolean current = plugin.getTeleportManager().getStorage().isTpIgnore(player.getUniqueId());
        final boolean next = !current;
        
        plugin.getTeleportManager().getStorage().setTpIgnore(player.getUniqueId(), next);
        MessageUtil.sendMessage(player, "<green>Teleport requests are now " + (next ? "<red>ignored" : "<green>accepted") + "!");
    }
}

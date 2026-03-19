package com.azox.utils.command.impl.teleport;

import com.azox.utils.command.BaseCommand;
import com.azox.utils.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class BackCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        final Optional<Location> lastLoc = plugin.getTeleportManager().getLastLocation(player);
        if (lastLoc.isEmpty()) {
            MessageUtil.sendMessage(player, "<red>No back location found!");
            return;
        }

        plugin.getTeleportManager().teleportWithDelay(player, lastLoc.get());
    }
}

package net.azox.cmd.command.impl.teleport;

import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.util.MessageUtil;
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

package net.azox.cmd.command.impl.teleport;

import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class TpaCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length == 0) {
            MessageUtil.sendMessage(player, "<red>Usage: /tpa <player>");
            return;
        }

        final Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtil.sendMessage(player, "<red>Player not found!");
            return;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            MessageUtil.sendMessage(player, "<red>You cannot teleport to yourself!");
            return;
        }

        if (this.plugin.getPlayerStorage().isTpIgnore(target)) {
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " That player is currently ignoring tp requests!");
            return;
        }

        this.plugin.getTeleportManager().requestTeleport(player, target, false);
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Teleport request sent to " + target.getName() + "!");

        Component accept = Component.text("[Accept] ", NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("/tpaccept " + player.getName()));
        Component deny = Component.text("[Deny]", NamedTextColor.RED).clickEvent(ClickEvent.runCommand("/tpdecline " + player.getName()));
        Component message = Component.text(player.getName() + " would like to teleport to you\n", NamedTextColor.YELLOW)
                .append(accept)
                .append(Component.text(" / ", NamedTextColor.GRAY))
                .append(deny);
        target.sendMessage(message);
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return getVisiblePlayerNames(sender, args[0]);
        }
        return new ArrayList<>();
    }
}
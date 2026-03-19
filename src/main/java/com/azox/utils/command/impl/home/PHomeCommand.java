package com.azox.utils.command.impl.home;

import com.azox.utils.command.BaseCommand;
import com.azox.utils.model.Home;
import com.azox.utils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class PHomeCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length == 0) {
            final List<Home> publicHomes = plugin.getHomeManager().getPublicHomes();
            if (publicHomes.isEmpty()) {
                MessageUtil.sendMessage(player, "<red>No public homes found!");
                return;
            }

            MessageUtil.sendMessage(player, "<gold>Public Homes:");
            for (final Home home : publicHomes) {
                final OfflinePlayer owner = Bukkit.getOfflinePlayer(home.getOwnerUuid());
                final Component comp = Component.text("[" + owner.getName() + ":" + home.getName() + "]", NamedTextColor.YELLOW)
                        .hoverEvent(HoverEvent.showText(Component.text("Description: " + (home.getDescription().isEmpty() ? "None" : home.getDescription()), NamedTextColor.GRAY)))
                        .clickEvent(ClickEvent.runCommand("/phome " + owner.getName() + ":" + home.getName()));
                player.sendMessage(comp);
            }
            return;
        }

        final String input = args[0];
        final String[] parts = input.split(":");
        final String targetName = parts[0];
        final String homeName = parts.length > 1 ? parts[1] : "home";

        final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        final Optional<Home> homeOpt = plugin.getHomeManager().getHome(target.getUniqueId(), homeName);

        if (homeOpt.isEmpty() || !homeOpt.get().isPublic()) {
            MessageUtil.sendMessage(player, "<red>Public home '" + input + "' not found!");
            return;
        }

        plugin.getTeleportManager().teleportWithDelay(player, homeOpt.get().getLocation());
    }
}

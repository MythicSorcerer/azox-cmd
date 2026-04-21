package net.azox.cmd.command.impl.util;

import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class KitCommands extends BaseCommand {

    @Override
    public void execute(final CommandSender sender, final String label, final String[] args) {
        if (label == null || args == null) {
            return;
        }
        if (!isPlayer(sender)) {
            return;
        }
        final Player player = (Player) sender;

        if (label.equalsIgnoreCase("createkit") || label.equalsIgnoreCase("ck")) {
            if (permissionDenied(sender, "azox.util.kit.create")) {
                return;
            }
            if (args.length == 0) {
                MessageUtil.sendMessage(player, "<red>Usage: /createkit <name> [cooldown]");
                return;
            }
            final String name = args[0];
            if (name.isBlank()) {
                MessageUtil.sendMessage(player, "<red>Usage: /createkit <name> [cooldown]");
                return;
            }
            long cooldown = 0L;
            if (args.length > 1) {
                try {
                    cooldown = Long.parseLong(args[1]);
                } catch (NumberFormatException ignored) {
                    cooldown = 0L;
                }
            }

            final ItemStack[] contents = player.getInventory().getContents();
            this.plugin.getKitManager().createKit(name, contents, cooldown);
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Kit '" + name + "' created with " + cooldown + "s cooldown!");
            return;
        }

        if (label.equalsIgnoreCase("delkit") || label.equalsIgnoreCase("rmkit")) {
            if (permissionDenied(sender, "azox.util.kit.delete")) {
                return;
            }
            if (args.length == 0) {
                MessageUtil.sendMessage(player, "<red>Usage: /delkit <name>");
                return;
            }
            final String name = args[0];
            if (name.isBlank()) {
                MessageUtil.sendMessage(player, "<red>Usage: /delkit <name>");
                return;
            }
            if (this.plugin.getKitManager().getKit(name).isEmpty()) {
                MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Kit not found!");
                return;
            }
            this.plugin.getKitManager().deleteKit(name);
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Kit deleted!");
            return;
        }

        if (label.equalsIgnoreCase("kit")) {
            if (args.length == 0) {
                final String kits = String.join(", ", this.plugin.getKitManager().getKits().keySet());
                MessageUtil.sendMessage(player, "<gold>Kits: <yellow>" + (kits.isEmpty() ? "None" : kits));
                return;
            }
            this.plugin.getKitManager().getKit(args[0]).ifPresentOrElse(
                    kit -> this.plugin.getKitManager().giveKit(player, kit),
                    () -> MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Kit not found!")
            );
        }
    }

    @Override
    public List<String> complete(final CommandSender sender, final String[] args) {
        if (args == null) {
            return new ArrayList<>();
        }
        if (args.length == 1) {
            return this.plugin.getKitManager().getKits().keySet().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

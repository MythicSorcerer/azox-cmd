package com.azox.utils.command.impl.util;

import com.azox.utils.command.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class VanishCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length > 0) {
            final String sub = args[0].toLowerCase();
            switch (sub) {
                case "tipu":
                    plugin.getVanishManager().toggleItemPickup(player);
                    return;
                case "fakejoin":
                case "fj":
                    plugin.getVanishManager().fakeJoin(player);
                    return;
                case "fakeleave":
                case "fakequit":
                case "fl":
                case "fq":
                    plugin.getVanishManager().fakeQuit(player);
                    return;
                case "gui":
                    plugin.getGuiManager().openVanishGui(player);
                    return;
            }
        }

        plugin.getVanishManager().toggleVanish(player);
    }
}

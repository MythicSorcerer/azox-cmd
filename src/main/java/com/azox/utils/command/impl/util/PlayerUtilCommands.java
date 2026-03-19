package com.azox.utils.command.impl.util;

import com.azox.utils.command.BaseCommand;
import com.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PlayerUtilCommands extends BaseCommand {

    private final String type;

    public PlayerUtilCommands(final String type) {
        this.type = type;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player target = sender instanceof Player ? (Player) sender : null;
        if (args.length > 0 && sender.hasPermission("azox.utils." + type + ".others")) {
            target = Bukkit.getPlayer(args[0]);
        }

        if (target == null) {
            MessageUtil.sendMessage(sender, "<red>Player not found!");
            return;
        }

        final Player finalTarget = target;
        switch (type.toLowerCase()) {
            case "feed":
                finalTarget.setFoodLevel(20);
                finalTarget.setSaturation(20);
                MessageUtil.sendMessage(finalTarget, "<green>You have been fed!");
                if (!finalTarget.equals(sender)) MessageUtil.sendMessage(sender, "<green>Fed " + finalTarget.getName() + "!");
                break;
            case "heal":
                finalTarget.setHealth(finalTarget.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue());
                finalTarget.setFoodLevel(20);
                finalTarget.setFireTicks(0);
                finalTarget.getActivePotionEffects().forEach(effect -> finalTarget.removePotionEffect(effect.getType()));
                MessageUtil.sendMessage(finalTarget, "<green>You have been healed!");
                if (!finalTarget.equals(sender)) MessageUtil.sendMessage(sender, "<green>Healed " + finalTarget.getName() + "!");
                break;
            case "fly":
                finalTarget.setAllowFlight(!finalTarget.getAllowFlight());
                MessageUtil.sendMessage(finalTarget, "<green>Flight " + (finalTarget.getAllowFlight() ? "enabled" : "disabled") + "!");
                if (!finalTarget.equals(sender)) MessageUtil.sendMessage(sender, "<green>Flight " + (finalTarget.getAllowFlight() ? "enabled" : "disabled") + " for " + finalTarget.getName() + "!");
                break;
            case "god":
                finalTarget.setInvulnerable(!finalTarget.isInvulnerable());
                MessageUtil.sendMessage(finalTarget, "<green>God mode " + (finalTarget.isInvulnerable() ? "enabled" : "disabled") + "!");
                if (!finalTarget.equals(sender)) MessageUtil.sendMessage(sender, "<green>God mode " + (finalTarget.isInvulnerable() ? "enabled" : "disabled") + " for " + finalTarget.getName() + "!");
                break;
        }
    }
}

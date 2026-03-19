package com.azox.utils.command.impl.util;

import com.azox.utils.command.BaseCommand;
import com.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public final class JailCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (label.equalsIgnoreCase("setjail")) {
            if (!isPlayer(sender)) return;
            if (args.length == 0) {
                MessageUtil.sendMessage(sender, "<red>Usage: /setjail <name>");
                return;
            }
            plugin.getJailManager().setJail(args[0], ((Player) sender).getLocation());
            MessageUtil.sendMessage(sender, "<green>Jail '" + args[0] + "' set!");
            return;
        }

        if (label.equalsIgnoreCase("deljail")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(sender, "<red>Usage: /deljail <name>");
                return;
            }
            plugin.getJailManager().deleteJail(args[0]);
            MessageUtil.sendMessage(sender, "<green>Jail '" + args[0] + "' deleted!");
            return;
        }

        if (label.equalsIgnoreCase("unjail")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(sender, "<red>Usage: /unjail <player>");
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageUtil.sendMessage(sender, "<red>Player not found!");
                return;
            }
            plugin.getTeleportManager().getStorage().setUnjailed(target.getUniqueId());
            MessageUtil.sendMessage(sender, "<green>Player " + target.getName() + " unjailed!");
            return;
        }

        // /jail <player> <jailname> [escapable|not] [dramatic]
        if (args.length < 2) {
            MessageUtil.sendMessage(sender, "<red>Usage: /jail <player> <jailname> [escapable|not] [dramatic]");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtil.sendMessage(sender, "<red>Player not found!");
            return;
        }

        Optional<Location> jailLoc = plugin.getJailManager().getJail(args[1]);
        if (jailLoc.isEmpty()) {
            MessageUtil.sendMessage(sender, "<red>Jail not found!");
            return;
        }

        boolean inescapable = args.length > 2 && args[2].equalsIgnoreCase("not");
        boolean dramatic = args.length > 3 && args[3].equalsIgnoreCase("dramatic");

        if (dramatic) {
            applyDramaticJail(target, jailLoc.get(), inescapable);
        } else {
            target.teleport(jailLoc.get());
            plugin.getTeleportManager().getStorage().setJailed(target.getUniqueId(), args[1], inescapable);
            MessageUtil.sendMessage(target, "<red>You have been jailed!");
        }
        MessageUtil.sendMessage(sender, "<green>Player " + target.getName() + " jailed in " + args[1] + " (" + (inescapable ? "inescapable" : "escapable") + ")");
    }

    private void applyDramaticJail(Player target, Location jailLoc, boolean inescapable) {
        boolean canLevitate = true;
        for (int i = 1; i <= 15; i++) {
            if (target.getLocation().add(0, i, 0).getBlock().getType() != Material.AIR) {
                canLevitate = false;
                break;
            }
        }

        if (canLevitate) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 2));
        }
        
        target.setGlowing(true);
        // We need a way to set glow color to red. Bukkit doesn't have a direct "setGlowColor"
        // Usually done via Teams. Let's just stick to glowing for now or assume a helper.
        
        new BukkitRunnable() {
            @Override
            public void run() {
                target.getWorld().strikeLightningEffect(target.getLocation());
                target.teleport(jailLoc);
                target.setGlowing(false);
                target.removePotionEffect(PotionEffectType.LEVITATION);
                plugin.getTeleportManager().getStorage().setJailed(target.getUniqueId(), "dramatic_jail", inescapable); // simplify name
                MessageUtil.sendMessage(target, "<red><bold>YOU HAVE BEEN JUDGED AND JAILED!");
            }
        }.runTaskLater(plugin, 40L);
    }
}

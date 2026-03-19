package com.azox.utils.manager;

import com.azox.utils.AzoxUtils;
import com.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class VanishManager {

    private final Set<UUID> vanishedPlayers = new HashSet<>();
    private final Set<UUID> noPickupPlayers = new HashSet<>();
    private final AzoxUtils plugin = AzoxUtils.getInstance();

    public void toggleVanish(final Player player) {
        if (vanishedPlayers.contains(player.getUniqueId())) {
            unvanish(player);
        } else {
            vanish(player);
        }
    }

    public void vanish(final Player player) {
        vanishedPlayers.add(player.getUniqueId());

        // Apply Preferences
        if (plugin.getTeleportManager().getStorage().isVanishPickupDisabled(player.getUniqueId())) {
            noPickupPlayers.add(player.getUniqueId());
        }

        if (plugin.getTeleportManager().getStorage().isVanishFakeMessages(player.getUniqueId())) {
            fakeQuit(player);
        }

        if (plugin.getTeleportManager().getStorage().isVanishAutoFly(player.getUniqueId())) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        if (plugin.getTeleportManager().getStorage().isVanishAutoGod(player.getUniqueId())) {
            player.setInvulnerable(true);
        }

        for (final Player other : Bukkit.getOnlinePlayers()) {
            if (!other.hasPermission("azox.utils.vanish.see") && !other.equals(player)) {
                other.hidePlayer(plugin, player);
            }
        }
        player.setMetadata("vanished", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " You are now vanished!");
    }

    public void unvanish(final Player player) {
        vanishedPlayers.remove(player.getUniqueId());
        noPickupPlayers.remove(player.getUniqueId());

        if (plugin.getTeleportManager().getStorage().isVanishFakeMessages(player.getUniqueId())) {
            fakeJoin(player);
        }

        for (final Player other : Bukkit.getOnlinePlayers()) {
            other.showPlayer(plugin, player);
        }
        player.removeMetadata("vanished", plugin);
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE && player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setInvulnerable(false); // Disable god if we enabled it? Or just safe default.
        }
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " You are no longer vanished!");
    }


    public void toggleItemPickup(final Player player) {
        if (noPickupPlayers.contains(player.getUniqueId())) {
            noPickupPlayers.remove(player.getUniqueId());
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Item pickup enabled.");
        } else {
            noPickupPlayers.add(player.getUniqueId());
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Item pickup disabled.");
        }
    }

    public boolean canPickup(final UUID uuid) {
        return !noPickupPlayers.contains(uuid);
    }

    public void fakeJoin(final Player player) {
        Bukkit.broadcast(MessageUtil.parse("<yellow>" + player.getName() + " joined the game"));
    }

    public void fakeQuit(final Player player) {
        Bukkit.broadcast(MessageUtil.parse("<yellow>" + player.getName() + " left the game"));
    }

    public boolean isVanished(final UUID uuid) {
        return vanishedPlayers.contains(uuid);
    }

    public void handleJoin(final Player joiningPlayer) {
        for (final UUID uuid : vanishedPlayers) {
            final Player vanished = Bukkit.getPlayer(uuid);
            if (vanished != null && !joiningPlayer.hasPermission("azox.utils.vanish.see")) {
                joiningPlayer.hidePlayer(plugin, vanished);
            }
        }
    }
}

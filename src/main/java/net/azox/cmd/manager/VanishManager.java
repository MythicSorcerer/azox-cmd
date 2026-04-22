package net.azox.cmd.manager;

import net.azox.cmd.AzoxCmd;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class VanishManager {

    private final Set<UUID> vanishedPlayers;
    private final Set<UUID> noPickupPlayers;
    private final AzoxCmd plugin = AzoxCmd.getInstance();

    public VanishManager() {
        this.vanishedPlayers = new HashSet<>();
        this.noPickupPlayers = new HashSet<>();
    }

    public void toggleVanish(final Player player) {
        if (player == null) {
            return;
        }
        if (this.vanishedPlayers.contains(player.getUniqueId())) {
            this.unvanish(player);
        } else {
            this.vanish(player);
        }
    }

    public void vanish(final Player player) {
        if (player == null) {
            return;
        }
        this.vanishedPlayers.add(player.getUniqueId());

        if (this.plugin.getPlayerStorage().isVanishPickupDisabled(player)) {
            this.noPickupPlayers.add(player.getUniqueId());
        }

        if (this.plugin.getPlayerStorage().isVanishFakeMessages(player)) {
            this.fakeQuit(player);
        }

        if (this.plugin.getPlayerStorage().isVanishAutoFly(player)) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        if (this.plugin.getPlayerStorage().isVanishAutoGod(player)) {
            player.setInvulnerable(true);
        }

        for (final Player other : Bukkit.getOnlinePlayers()) {
            if (!this.canSee(other, player)) {
                other.hidePlayer(this.plugin, player);
            }
        }
        player.setMetadata("vanished", new org.bukkit.metadata.FixedMetadataValue(this.plugin, true));
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " You are now vanished (Level " + this.getVanishLevel(player) + ")!");
    }

    public void unvanish(final Player player) {
        if (player == null) {
            return;
        }
        this.vanishedPlayers.remove(player.getUniqueId());
        this.noPickupPlayers.remove(player.getUniqueId());

        if (this.plugin.getPlayerStorage().isVanishFakeMessages(player)) {
            this.fakeJoin(player);
        }

        for (final Player other : Bukkit.getOnlinePlayers()) {
            other.showPlayer(this.plugin, player);
        }
        player.removeMetadata("vanished", this.plugin);
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE && player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setInvulnerable(false);
        }
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " You are no longer vanished!");
    }

    public boolean canSee(final Player viewer, final Player target) {
        if (viewer == null || target == null) {
            return true;
        }
        if (!this.isVanished(target.getUniqueId())) {
            return true;
        }
        if (viewer.getUniqueId().equals(target.getUniqueId())) {
            return true;
        }
        if (!viewer.hasPermission("azox.util.vanish.see")) {
            return false;
        }

        return this.getVanishLevel(viewer) >= this.getVanishLevel(target);
    }

    public int getVanishLevel(final Player player) {
        if (player == null) {
            return 1;
        }
        for (int level = 100; level > 0; level--) {
            if (player.hasPermission("azox.util.vanish.level." + level)) {
                if (level > 3 && !player.isPermissionSet("azox.util.vanish.level." + level) && player.isOp()) {
                    continue;
                }
                return level;
            }
        }
        return player.isOp() ? 3 : 1;
    }

    public void toggleItemPickup(final Player player) {
        if (player == null) {
            return;
        }
        final boolean currentlyDisabled = this.plugin.getPlayerStorage().isVanishPickupDisabled(player);
        this.plugin.getPlayerStorage().setVanishPickupDisabled(player, !currentlyDisabled);

        if (!currentlyDisabled) {
            this.noPickupPlayers.add(player.getUniqueId());
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Item pickup disabled.");
        } else {
            this.noPickupPlayers.remove(player.getUniqueId());
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Item pickup enabled.");
        }
    }

    public boolean canPickup(final UUID uuid) {
        return uuid != null && !this.noPickupPlayers.contains(uuid);
    }

    public void fakeJoin(final Player player) {
        if (player == null) {
            return;
        }
        Bukkit.broadcast(MessageUtil.parse("<yellow>" + player.getName() + " joined the game"));
    }

    public void fakeQuit(final Player player) {
        if (player == null) {
            return;
        }
        Bukkit.broadcast(MessageUtil.parse("<yellow>" + player.getName() + " left the game"));
    }

    public boolean isVanished(final UUID uuid) {
        return uuid != null && this.vanishedPlayers.contains(uuid);
    }

    public void handleJoin(final Player joiningPlayer) {
        if (joiningPlayer == null) {
            return;
        }
        for (final UUID uuid : this.vanishedPlayers) {
            final Player vanished = Bukkit.getPlayer(uuid);
            if (vanished != null && !joiningPlayer.hasPermission("azox.util.vanish.see")) {
                joiningPlayer.hidePlayer(this.plugin, vanished);
            }
        }
    }
}

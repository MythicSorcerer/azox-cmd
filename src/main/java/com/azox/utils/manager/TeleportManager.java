package com.azox.utils.manager;

import com.azox.utils.AzoxUtils;
import com.azox.utils.model.TeleportRequest;
import com.azox.utils.storage.PlayerStorage;
import com.azox.utils.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportManager {

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    private final Map<UUID, TeleportRequest> pendingRequests = new ConcurrentHashMap<>();
    private final Map<UUID, Location> lastLocations = new ConcurrentHashMap<>();
    private final Map<UUID, Location> pendingOfflineTeleports = new ConcurrentHashMap<>();
    private final Map<UUID, Location> undoLocations = new ConcurrentHashMap<>();

    public TeleportManager() {
    }

    public void addPendingTeleport(final UUID target, final Location destination) {
        if (target == null || destination == null) {
            return;
        }
        pendingOfflineTeleports.put(target, destination);
    }

    public Location getPendingTeleport(final UUID target) {
        if (target == null) {
            return null;
        }
        return pendingOfflineTeleports.remove(target);
    }

    public void addUndoLocation(final UUID target, final Location oldLocation) {
        if (target == null || oldLocation == null) {
            return;
        }
        undoLocations.put(target, oldLocation);
    }

    public Location getUndoLocation(final UUID target) {
        if (target == null) {
            return null;
        }
        return undoLocations.remove(target);
    }

    public void requestTeleport(final Player requester, final Player target, final boolean here) {
        if (requester == null || target == null) {
            return;
        }
        final TeleportRequest request = new TeleportRequest(requester, target, here, System.currentTimeMillis());
        pendingRequests.put(target.getUniqueId(), request);
    }

    public Optional<TeleportRequest> getRequest(final Player target) {
        if (target == null) {
            return Optional.empty();
        }
        final TeleportRequest request = pendingRequests.get(target.getUniqueId());
        if (request != null && request.isExpired()) {
            pendingRequests.remove(target.getUniqueId());
            return Optional.empty();
        }
        return Optional.ofNullable(request);
    }

    public void removeRequest(final Player target) {
        if (target == null) {
            return;
        }
        pendingRequests.remove(target.getUniqueId());
    }

    public void teleportWithDelay(final Player player, final Location targetLocation) {
        if (player == null || targetLocation == null) {
            return;
        }
        final int delay = 3;

        if (delay <= 0 || player.hasPermission("azox.utils.teleport.instant")) {
            setLastLocation(player, player.getLocation());
            player.teleport(targetLocation);
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Teleported!");
            return;
        }

        MessageUtil.sendMessage(player, "<yellow>" + MessageUtil.ICON_TP + " Teleporting in " + delay + " seconds, please do not move...");
        final Location startLocation = player.getLocation();

        new BukkitRunnable() {
            private int count = delay;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (player.getLocation().distanceSquared(startLocation) > 0.1) {
                    MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Teleport cancelled because you moved!");
                    cancel();
                    return;
                }

                if (count <= 0) {
                    setLastLocation(player, player.getLocation());
                    player.teleport(targetLocation);
                    MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Teleported!");
                    cancel();
                    return;
                }

                count--;
            }
        }.runTaskTimer(AzoxUtils.getInstance(), 0L, 20L);
    }

    public void setLastLocation(final Player player, final Location location) {
        if (player == null || location == null) {
            return;
        }
        lastLocations.put(player.getUniqueId(), location);
        plugin.getPlayerStorage().setBackLocation(player, location);
    }

    public Optional<Location> getLastLocation(final Player player) {
        if (player == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(lastLocations.get(player.getUniqueId()))
                .or(() -> Optional.ofNullable(plugin.getPlayerStorage().getBackLocation(player)));
    }
}

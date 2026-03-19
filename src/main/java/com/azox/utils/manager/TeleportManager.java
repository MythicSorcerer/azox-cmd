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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportManager {

    @Getter
    private final PlayerStorage storage;
    private final Map<UUID, TeleportRequest> pendingRequests = new ConcurrentHashMap<>();
    private final Map<UUID, Location> lastLocations = new ConcurrentHashMap<>();

    public TeleportManager() {
        this.storage = new PlayerStorage();
    }

    public void requestTeleport(final Player requester, final Player target, final boolean here) {
        final TeleportRequest request = new TeleportRequest(requester, target, here, System.currentTimeMillis());
        pendingRequests.put(target.getUniqueId(), request);
    }

    public Optional<TeleportRequest> getRequest(final Player target) {
        final TeleportRequest request = pendingRequests.get(target.getUniqueId());
        if (request != null && request.isExpired()) {
            pendingRequests.remove(target.getUniqueId());
            return Optional.empty();
        }
        return Optional.ofNullable(request);
    }

    public void removeRequest(final Player target) {
        pendingRequests.remove(target.getUniqueId());
    }

    public void teleportWithDelay(final Player player, final Location targetLocation) {
        // Handle ranks hook for delay later. Default 3s.
        final int delay = 3;
        
        if (delay <= 0 || player.hasPermission("azox.utils.teleport.instant")) {
            this.setLastLocation(player, player.getLocation());
            player.teleport(targetLocation);
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Teleported!");
            return;
        }

        MessageUtil.sendMessage(player, "<yellow>" + MessageUtil.ICON_TP + " Teleporting in " + delay + " seconds, please do not move...");
        final Location startLocation = player.getLocation();

        new BukkitRunnable() {
            int count = delay;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                if (player.getLocation().distanceSquared(startLocation) > 0.1) {
                    MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Teleport cancelled because you moved!");
                    this.cancel();
                    return;
                }

                if (count <= 0) {
                    setLastLocation(player, player.getLocation());
                    player.teleport(targetLocation);
                    MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Teleported!");
                    this.cancel();
                    return;
                }

                count--;
            }
        }.runTaskTimer(AzoxUtils.getInstance(), 0L, 20L);
    }

    public void setLastLocation(final Player player, final Location location) {
        lastLocations.put(player.getUniqueId(), location);
        storage.setBackLocation(player.getUniqueId(), location);
    }

    public Optional<Location> getLastLocation(final Player player) {
        return Optional.ofNullable(lastLocations.get(player.getUniqueId()))
                .or(() -> Optional.ofNullable(storage.getBackLocation(player.getUniqueId())));
    }
}

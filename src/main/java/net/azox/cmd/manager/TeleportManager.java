package net.azox.cmd.manager;

import net.azox.cmd.AzoxCmd;
import net.azox.cmd.model.TeleportRequest;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportManager {

    private static final int TELEPORT_DELAY_SECONDS = 3;
    private static final long TELEPORT_EXPIRY_MILLIS = 60_000;
    private static final double MOVE_THRESHOLD_SQUARED = 0.1;

    private final AzoxCmd plugin = AzoxCmd.getInstance();
    private final Map<UUID, TeleportRequest> pendingRequests;
    private final Map<UUID, Location> lastLocations;
    private final Map<UUID, Location> pendingOfflineTeleports;
    private final Map<UUID, Location> undoLocations;

    public TeleportManager() {
        this.pendingRequests = new ConcurrentHashMap<>();
        this.lastLocations = new ConcurrentHashMap<>();
        this.pendingOfflineTeleports = new ConcurrentHashMap<>();
        this.undoLocations = new ConcurrentHashMap<>();
    }

    public void addPendingTeleport(final UUID target, final Location destination) {
        if (target != null && destination != null) {
            this.pendingOfflineTeleports.put(target, destination);
        }
    }

    public Location getPendingTeleport(final UUID target) {
        return target != null ? this.pendingOfflineTeleports.remove(target) : null;
    }

    public void addUndoLocation(final UUID target, final Location oldLocation) {
        if (target != null && oldLocation != null) {
            this.undoLocations.put(target, oldLocation);
        }
    }

    public Location getUndoLocation(final UUID target) {
        return target != null ? this.undoLocations.remove(target) : null;
    }

    public void requestTeleport(final Player requester, final Player target, final boolean here) {
        if (requester != null && target != null) {
            this.pendingRequests.put(target.getUniqueId(), new TeleportRequest(requester, target, here, System.currentTimeMillis()));
        }
    }

    public Optional<TeleportRequest> getRequest(final Player target) {
        if (target == null) {
            return Optional.empty();
        }
        final TeleportRequest request = this.pendingRequests.get(target.getUniqueId());
        if (request != null && request.isExpired()) {
            this.pendingRequests.remove(target.getUniqueId());
            return Optional.empty();
        }
        return Optional.ofNullable(request);
    }

    public void removeRequest(final Player target) {
        if (target != null) {
            this.pendingRequests.remove(target.getUniqueId());
        }
    }

    public void teleportWithDelay(final Player player, final Location targetLocation) {
        if (player == null || targetLocation == null) {
            return;
        }

        if (player.hasPermission("azox.util.teleport.instant")) {
            teleportInstantly(player, targetLocation);
            return;
        }

        if (TELEPORT_DELAY_SECONDS <= 0) {
            teleportInstantly(player, targetLocation);
            return;
        }

        scheduleDelayedTeleport(player, targetLocation);
    }

    private void teleportInstantly(final Player player, final Location targetLocation) {
        this.setLastLocation(player, player.getLocation());
        player.teleport(targetLocation);
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Teleported!");
    }

    private void scheduleDelayedTeleport(final Player player, final Location targetLocation) {
        MessageUtil.sendMessage(player, "<yellow>" + MessageUtil.ICON_TP + " Teleporting in " + TELEPORT_DELAY_SECONDS + " seconds, please do not move...");
        final Location startLocation = player.getLocation();

        new BukkitRunnable() {
            private int count = TELEPORT_DELAY_SECONDS;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (player.getLocation().distanceSquared(startLocation) > MOVE_THRESHOLD_SQUARED) {
                    MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Teleport cancelled because you moved!");
                    cancel();
                    return;
                }

                if (count <= 0) {
                    TeleportManager.this.setLastLocation(player, player.getLocation());
                    player.teleport(targetLocation);
                    MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Teleported!");
                    cancel();
                    return;
                }

                count--;
            }
        }.runTaskTimer(this.plugin, 0L, 20L);
    }

    public void setLastLocation(final Player player, final Location location) {
        if (player != null && location != null) {
            this.lastLocations.put(player.getUniqueId(), location);
            this.plugin.getPlayerStorage().setBackLocation(player, location);
        }
    }

    public Optional<Location> getLastLocation(final Player player) {
        if (player == null) {
            return Optional.empty();
        }
        final Location cached = this.lastLocations.get(player.getUniqueId());
        if (cached != null) {
            return Optional.of(cached);
        }
        return Optional.ofNullable(this.plugin.getPlayerStorage().getBackLocation(player));
    }
}

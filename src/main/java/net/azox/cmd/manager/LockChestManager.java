package net.azox.cmd.manager;

import net.azox.cmd.AzoxCmd;
import net.azox.cmd.model.ChestLock;
import net.azox.cmd.model.ChestLog;
import net.azox.cmd.storage.ChestLogStorage;
import net.azox.cmd.storage.ChestStorage;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;

public final class LockChestManager {

    private final AzoxCmd plugin = AzoxCmd.getInstance();
    private final ChestStorage chestStorage;
    private final ChestLogStorage chestLogStorage;

    public LockChestManager() {
        this.chestStorage = new ChestStorage();
        this.chestLogStorage = new ChestLogStorage();
    }

    public boolean isEnabled() {
        return this.plugin.getConfig().getBoolean("chestlocking.enabled", true);
    }

    public boolean isGlobalLogging() {
        return this.plugin.getConfig().getBoolean("chestlocking.global_log", false);
    }

    public ChestLock lockChest(final org.bukkit.entity.Player player, final Block block) {
        if (player == null || block == null || !this.isEnabled()) {
            return null;
        }

        final Location pos = block.getLocation();
        final String worldName = pos.getWorld().getName();
        final int bx = pos.getBlockX();
        final int by = pos.getBlockY();
        final int bz = pos.getBlockZ();

        if (this.chestStorage.getChestAtLocation(worldName, bx, by, bz) != null) {
            return null;
        }

        int[] adjacentOffsets = {-1, 1};
        for (int offset : adjacentOffsets) {
            if (this.chestStorage.getChestAtLocation(worldName, bx + offset, by, bz) != null) {
                return null;
            }
            if (this.chestStorage.getChestAtLocation(worldName, bx, by, bz + offset) != null) {
                return null;
            }
        }

        String chestId = player.getName() + "_" + this.chestStorage.getNextChestNumber(player.getUniqueId());

        ChestLock chestLock = new ChestLock();
        chestLock.setOwnerUuid(player.getUniqueId());
        chestLock.setChestId(chestId);
        chestLock.setWorldName(worldName);
        chestLock.setX(bx);
        chestLock.setY(by);
        chestLock.setZ(bz);
        chestLock.setLocked(true);

        this.chestStorage.saveChest(chestLock);

        if (this.isGlobalLogging()) {
            ChestLog log = new ChestLog();
            log.setChestId(chestId);
            log.setWorldName(worldName);
            log.setX(bx);
            log.setY(by);
            log.setZ(bz);
            log.setOwnerUuid(player.getUniqueId());
            log.setAction(ChestLog.ChestAction.PLACE);
            log.setTimestamp(System.currentTimeMillis());
            this.chestLogStorage.logChestAction(log);
        }

        return chestLock;
    }

    public boolean unlockChest(String chestId) {
        if (chestId == null) {
            return false;
        }
        this.chestStorage.deleteChest(chestId);
        return true;
    }

    public ChestLock getChestAt(Location location) {
        if (location == null) {
            return null;
        }
        return this.chestStorage.getChestAtLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public ChestLock getChestById(String chestId) {
        if (chestId == null) {
            return null;
        }
        return this.chestStorage.getChest(chestId);
    }

    public void logChestAction(Location location, String chestId, ChestLog.ChestAction action) {
        if (location == null) {
            return;
        }
        if (!this.isGlobalLogging()) {
            return;
        }

        ChestLog log = new ChestLog();
        log.setChestId(chestId);
        log.setWorldName(location.getWorld().getName());
        log.setX(location.getBlockX());
        log.setY(location.getBlockY());
        log.setZ(location.getBlockZ());
        if (chestId != null) {
            ChestLock chest = this.chestStorage.getChest(chestId);
            if (chest != null) {
                log.setOwnerUuid(chest.getOwnerUuid());
            }
        }
        log.setAction(action);
        log.setTimestamp(System.currentTimeMillis());
        this.chestLogStorage.logChestAction(log);
    }

    public java.util.List<ChestLog> getChestLogs(String chestId, int limit) {
        return this.chestLogStorage.getLogsForChest(chestId, limit);
    }
}
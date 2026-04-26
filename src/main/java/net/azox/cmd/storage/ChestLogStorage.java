package net.azox.cmd.storage;

import net.azox.cmd.AzoxCmd;
import net.azox.cmd.model.ChestLog;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class ChestLogStorage extends BaseStorage {

    private static final int MAX_GLOBAL_LOGS = 10000;
    private final ConcurrentLinkedDeque<ChestLog> globalLogs = new ConcurrentLinkedDeque<>();

    public ChestLogStorage() {
        super("chestlogs.yml");
        this.loadGlobalLogs();
    }

    private void loadGlobalLogs() {
        if (this.config.getKeys(false).isEmpty()) {
            return;
        }
        for (final String key : this.config.getKeys(false)) {
            final ConfigurationSection section = this.config.getConfigurationSection(key);
            if (section != null) {
                final ChestLog log = new ChestLog();
                log.setChestId(section.getString("chestId"));
                log.setWorldName(section.getString("worldName"));
                log.setX(section.getInt("x"));
                log.setY(section.getInt("y"));
                log.setZ(section.getInt("z"));
                log.setOwnerUuid(section.contains("ownerUuid") ? UUID.fromString(section.getString("ownerUuid")) : null);
                log.setAction(ChestLog.ChestAction.valueOf(section.getString("action")));
                log.setTimestamp(section.getLong("timestamp"));
                this.globalLogs.add(log);
            }
        }
    }

    public void logChestAction(final ChestLog log) {
        if (log == null || log.getChestId() == null) {
            return;
        }
        final String logKey = log.getChestId() + "_" + log.getTimestamp();
        this.config.set(logKey + ".chestId", log.getChestId());
        this.config.set(logKey + ".worldName", log.getWorldName());
        this.config.set(logKey + ".x", log.getX());
        this.config.set(logKey + ".y", log.getY());
        this.config.set(logKey + ".z", log.getZ());
        if (log.getOwnerUuid() != null) {
            this.config.set(logKey + ".ownerUuid", log.getOwnerUuid().toString());
        }
        this.config.set(logKey + ".action", log.getAction().name());
        this.config.set(logKey + ".timestamp", log.getTimestamp());
        
        this.globalLogs.addFirst(log);
        while (this.globalLogs.size() > MAX_GLOBAL_LOGS) {
            this.globalLogs.removeLast();
        }
        this.save();
    }

    public List<ChestLog> getRecentLogs(final int limit) {
        final List<ChestLog> result = new ArrayList<>();
        int count = 0;
        for (final ChestLog log : this.globalLogs) {
            if (count >= limit) {
                break;
            }
            result.add(log);
            count++;
        }
        return result;
    }

    public List<ChestLog> getLogsForChest(final String chestId, final int limit) {
        final List<ChestLog> result = new ArrayList<>();
        for (final ChestLog log : this.globalLogs) {
            if (log.getChestId().equals(chestId)) {
                result.add(log);
                if (result.size() >= limit) {
                    break;
                }
            }
        }
        return result;
    }

    public void clearOldLogs() {
        final long cutoff = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
        this.globalLogs.removeIf(log -> log.getTimestamp() < cutoff);
        this.save();
    }
}
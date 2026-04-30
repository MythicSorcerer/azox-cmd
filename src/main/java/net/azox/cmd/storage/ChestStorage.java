package net.azox.cmd.storage;

import net.azox.cmd.AzoxCmd;
import net.azox.cmd.model.ChestLock;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ChestStorage extends BaseStorage {

    private final Map<String, ChestLock> locationCache = new ConcurrentHashMap<>();

    public ChestStorage() {
        super("chestlocks.yml");
        this.refreshCache();
    }

    public void refreshCache() {
        this.locationCache.clear();
        for (final ChestLock chest : this.getAllChests().values()) {
            if (chest.getChestId() != null) {
                this.locationCache.put(this.locationKey(chest.getWorldName(), chest.getX(), chest.getY(), chest.getZ()), chest);
            }
        }
    }

    private String locationKey(final String world, final int x, final int y, final int z) {
        return world + "_" + x + "_" + y + "_" + z;
    }

    public void saveChest(final ChestLock chest) {
        if (chest == null || chest.getChestId() == null) {
            return;
        }
        final String path = chest.getChestId();
        this.config.set(path + ".ownerUuid", chest.getOwnerUuid().toString());
        this.config.set(path + ".worldName", chest.getWorldName());
        this.config.set(path + ".x", chest.getX());
        this.config.set(path + ".y", chest.getY());
        this.config.set(path + ".z", chest.getZ());
        this.config.set(path + ".allowedPlayers", chest.getAllowedPlayers() != null ?
            chest.getAllowedPlayers().stream().map(UUID::toString).toList() : new ArrayList<>());
        this.config.set(path + ".creationDate", chest.getCreationDate());
        this.config.set(path + ".locked", chest.isLocked());
        this.save();
        this.refreshCache();
    }

    public void deleteChest(final String chestId) {
        if (chestId == null) {
            return;
        }
        final var existing = this.getChest(chestId);
        if (existing != null) {
            this.locationCache.remove(this.locationKey(existing.getWorldName(), existing.getX(), existing.getY(), existing.getZ()));
        }
        this.config.set(chestId, null);
        this.save();
    }

    public ChestLock getChest(final String chestId) {
        if (chestId == null || !this.config.contains(chestId)) {
            return null;
        }
        final ConfigurationSection section = this.config.getConfigurationSection(chestId);
        if (section == null) {
            return null;
        }
        final ChestLock chest = new ChestLock();
        chest.setChestId(chestId);
        chest.setOwnerUuid(UUID.fromString(section.getString("ownerUuid")));
        chest.setWorldName(section.getString("worldName"));
        chest.setX(section.getInt("x"));
        chest.setY(section.getInt("y"));
        chest.setZ(section.getInt("z"));
        final List<String> allowedList = section.getStringList("allowedPlayers");
        chest.setAllowedPlayers(allowedList != null ?
            allowedList.stream().map(UUID::fromString).toList() : new ArrayList<>());
        chest.setCreationDate(section.getLong("creationDate"));
        chest.setLocked(section.getBoolean("locked", true));
        return chest;
    }

    public Map<String, ChestLock> getAllChests() {
        final Map<String, ChestLock> chests = new HashMap<>();
        if (this.config.getKeys(false).isEmpty()) {
            return chests;
        }
        for (final String key : this.config.getKeys(false)) {
            final ChestLock chest = this.getChest(key);
            if (chest != null) {
                chests.put(key, chest);
            }
        }
        return chests;
    }

    public ChestLock getChestAtLocation(final String worldName, final int x, final int y, final int z) {
        return this.locationCache.get(this.locationKey(worldName, x, y, z));
    }

    public int getNextChestNumber(final UUID ownerUuid) {
        int maxNumber = 0;
        for (final String chestId : this.config.getKeys(false)) {
            try {
                final int lastUnderscore = chestId.lastIndexOf('_');
                if (lastUnderscore > 0) {
                    final UUID uuid = this.getChest(chestId).getOwnerUuid();
                    if (uuid != null && uuid.equals(ownerUuid)) {
                        final String numStr = chestId.substring(lastUnderscore + 1);
                        final int num = Integer.parseInt(numStr);
                        if (num > maxNumber) {
                            maxNumber = num;
                        }
                    }
                }
            } catch (final Exception ignored) {
            }
        }
        return maxNumber + 1;
    }

    public List<ChestLock> getChestsByOwner(final UUID ownerUuid) {
        final List<ChestLock> result = new ArrayList<>();
        for (final ChestLock chest : this.getAllChests().values()) {
            if (chest.getOwnerUuid().equals(ownerUuid)) {
                result.add(chest);
            }
        }
        return result;
    }
}
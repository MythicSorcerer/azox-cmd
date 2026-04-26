package net.azox.cmd.manager;

import net.azox.cmd.AzoxCmd;
import net.azox.cmd.model.ChestLock;
import net.azox.cmd.model.ChestLog;
import net.azox.cmd.model.KeyLog;
import net.azox.cmd.storage.ChestLogStorage;
import net.azox.cmd.storage.ChestStorage;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class LockChestManager {

    public static final NamespacedKey KEY_ID_KEY = new NamespacedKey(AzoxCmd.getInstance(), "key_id");
    public static final NamespacedKey CHEST_ID_KEY = new NamespacedKey(AzoxCmd.getInstance(), "chest_id");
    public static final NamespacedKey KEY_TYPE_KEY = new NamespacedKey(AzoxCmd.getInstance(), "key_type");
    public static final NamespacedKey KEY_ACTION_KEY = new NamespacedKey(AzoxCmd.getInstance(), "key_action");

    private final AzoxCmd plugin = AzoxCmd.getInstance();
    private final ChestStorage chestStorage;
    private final ChestLogStorage chestLogStorage;

    public LockChestManager() {
        this.chestStorage = new ChestStorage();
        this.chestLogStorage = new ChestLogStorage();
    }

    public ChestStorage getChestStorage() {
        return this.chestStorage;
    }

    public ChestLogStorage getChestLogStorage() {
        return this.chestLogStorage;
    }

    public boolean isEnabled() {
        return this.plugin.getConfig().getBoolean("chestlocking.enabled", true);
    }

    public boolean isGlobalLogging() {
        return this.plugin.getConfig().getBoolean("chestlocking.global_log", false);
    }

    public String getOwnerName(final UUID uuid) {
        if (uuid == null) {
            return "Unknown";
        }
        final var player = this.plugin.getServer().getPlayer(uuid);
        if (player != null) {
            return player.getName();
        }
        final var offlinePlayer = this.plugin.getServer().getOfflinePlayer(uuid);
        return offlinePlayer != null ? offlinePlayer.getName() : "Unknown";
    }

    public ChestLock lockChest(final org.bukkit.entity.Player player, final org.bukkit.block.Block block) {
        if (player == null || block == null || !this.isEnabled()) {
            return null;
        }

        final org.bukkit.Location pos = block.getLocation();
        final var worldName = pos.getWorld().getName();
        final var bx = pos.getBlockX();
        final var by = pos.getBlockY();
        final var bz = pos.getBlockZ();

        if (this.chestStorage.getChestAtLocation(worldName, bx, by, bz) != null) {
            return null;
        }

        final int[] adjacentOffsets = {-1, 1};
        for (final int offset : adjacentOffsets) {
            var adjacentLock = this.chestStorage.getChestAtLocation(worldName, bx + offset, by, bz);
            if (adjacentLock != null) {
                return null;
            }
            adjacentLock = this.chestStorage.getChestAtLocation(worldName, bx, by, bz + offset);
            if (adjacentLock != null) {
                return null;
            }
        }

        final String playerName = player.getName();
        final String chestId = playerName + "_" + this.chestStorage.getNextChestNumber(player.getUniqueId());

        final ChestLock chestLock = new ChestLock();
        chestLock.setOwnerUuid(player.getUniqueId());
        chestLock.setChestId(chestId);
        chestLock.setWorldName(worldName);
        chestLock.setX(bx);
        chestLock.setY(by);
        chestLock.setZ(bz);
        chestLock.setAllowedPlayers(new ArrayList<>());
        chestLock.setCreationDate(System.currentTimeMillis());
        chestLock.setLocked(true);

        this.chestStorage.saveChest(chestLock);

        if (this.isGlobalLogging()) {
            final ChestLog log = new ChestLog();
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

    public boolean unlockChest(final String chestId) {
        if (chestId == null) {
            return false;
        }
        this.chestStorage.deleteChest(chestId);
        return true;
    }

    public ChestLock getChestAt(final org.bukkit.Location location) {
        if (location == null) {
            return null;
        }
        return this.chestStorage.getChestAtLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public ChestLock getChestById(final String chestId) {
        if (chestId == null) {
            return null;
        }
        return this.chestStorage.getChest(chestId);
    }

    public boolean canAccess(final org.bukkit.entity.Player player, final ChestLock chest) {
        if (player == null || chest == null) {
            return false;
        }
        return chest.isAllowed(player.getUniqueId());
    }

    public boolean isKeyItem(final ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(KEY_ID_KEY, PersistentDataType.STRING);
    }

    public String getKeyChestId(final ItemStack item) {
        if (item == null || !this.isKeyItem(item)) {
            return null;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(CHEST_ID_KEY, PersistentDataType.STRING);
    }

    public UUID getKeyId(final ItemStack item) {
        if (item == null || !this.isKeyItem(item)) {
            return null;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        final String keyIdStr = meta.getPersistentDataContainer().get(KEY_ID_KEY, PersistentDataType.STRING);
        if (keyIdStr == null) {
            return null;
        }
        try {
            return UUID.fromString(keyIdStr);
        } catch (final IllegalArgumentException ignored) {
            return null;
        }
    }

    public ItemStack createPhysicalKey(final ChestLock chest) {
        if (chest == null) {
            return null;
        }

        final ItemStack key = new ItemStack(Material.PAPER);
        final ItemMeta meta = key.getItemMeta();
        if (meta == null) {
            return null;
        }

        final UUID keyId = UUID.randomUUID();
        final String ownerName = this.getOwnerName(chest.getOwnerUuid());

        meta.getPersistentDataContainer().set(KEY_ID_KEY, PersistentDataType.STRING, keyId.toString());
        meta.getPersistentDataContainer().set(CHEST_ID_KEY, PersistentDataType.STRING, chest.getChestId());
        meta.getPersistentDataContainer().set(KEY_TYPE_KEY, PersistentDataType.STRING, "PHYSICAL");

        meta.displayName(MessageUtil.parse("<gold>" + ownerName + " Chest Key #" + chest.getChestId().split("_")[1]));
        meta.lore(List.of(
            MessageUtil.parse("<gray>This key unlocks " + ownerName + "'s Chest #" + chest.getChestId().split("_")[1]),
            MessageUtil.parse("<gray>Right click while in mainhand to make virtual.")
        ));

        meta.setEnchantmentGlintOverride(true);

        key.setItemMeta(meta);
        key.setAmount(1);

        final KeyLog keyLog = new KeyLog();
        keyLog.setKeyId(keyId);
        keyLog.setChestId(chest.getChestId());
        keyLog.setOwnerUuid(chest.getOwnerUuid());
        keyLog.setType(KeyLog.KeyType.PHYSICAL);
        keyLog.setLocation(chest.getWorldName() + "," + chest.getX() + "," + chest.getY() + "," + chest.getZ());
        keyLog.setAction(KeyLog.KeyAction.CREATED);
        keyLog.setTimestamp(System.currentTimeMillis());
        this.logKeyAction(keyLog);

        return key;
    }

    public boolean addVirtualKey(final UUID playerUuid, final String chestId) {
        if (playerUuid == null || chestId == null) {
            return false;
        }

        final UUID keyId = UUID.randomUUID();
        final net.azox.cmd.model.ChestLock chest = this.chestStorage.getChest(chestId);
        if (chest == null) {
            return false;
        }

        chest.addAllowedPlayer(playerUuid);
        this.chestStorage.saveChest(chest);

        final KeyLog keyLog = new KeyLog();
        keyLog.setKeyId(keyId);
        keyLog.setChestId(chestId);
        keyLog.setOwnerUuid(chest.getOwnerUuid());
        keyLog.setType(KeyLog.KeyType.VIRTUAL);
        keyLog.setLocation("player_inventory");
        keyLog.setAction(KeyLog.KeyAction.GIVEN_TO_PLAYER);
        keyLog.setTimestamp(System.currentTimeMillis());
        this.logKeyAction(keyLog);

        return true;
    }

    public boolean removeVirtualKey(final UUID playerUuid, final String chestId) {
        if (playerUuid == null || chestId == null) {
            return false;
        }

        final ChestLock chest = this.chestStorage.getChest(chestId);
        if (chest == null) {
            return false;
        }

        chest.removeAllowedPlayer(playerUuid);
        this.chestStorage.saveChest(chest);

        final KeyLog keyLog = new KeyLog();
        keyLog.setKeyId(UUID.randomUUID());
        keyLog.setChestId(chestId);
        keyLog.setOwnerUuid(chest.getOwnerUuid());
        keyLog.setType(KeyLog.KeyType.VIRTUAL);
        keyLog.setLocation("player_inventory");
        keyLog.setAction(KeyLog.KeyAction.REMOVED_FROM_PLAYER);
        keyLog.setTimestamp(System.currentTimeMillis());
        this.logKeyAction(keyLog);

        return true;
    }

    public boolean convertPhysicalToVirtual(final org.bukkit.entity.Player player, final ItemStack keyItem) {
        if (player == null || keyItem == null || !this.isKeyItem(keyItem)) {
            return false;
        }

        final String chestId = this.getKeyChestId(keyItem);
        if (chestId == null) {
            return false;
        }

        final ChestLock chest = this.chestStorage.getChest(chestId);
        if (chest == null) {
            return false;
        }

        if (!chest.getOwnerUuid().equals(player.getUniqueId())) {
            return false;
        }

        final List<String> allowedList = new ArrayList<>();
        allowedList.add(player.getUniqueId().toString());
        chest.addAllowedPlayer(player.getUniqueId());
        this.chestStorage.saveChest(chest);

        final KeyLog keyLog = new KeyLog();
        keyLog.setKeyId(this.getKeyId(keyItem));
        keyLog.setChestId(chestId);
        keyLog.setOwnerUuid(chest.getOwnerUuid());
        keyLog.setType(KeyLog.KeyType.PHYSICAL);
        keyLog.setLocation(player.getLocation() != null ?
            player.getLocation().getWorld().getName() + "," + (int) player.getLocation().getX() + "," +
                (int) player.getLocation().getY() + "," + (int) player.getLocation().getZ() : "unknown");
        keyLog.setAction(KeyLog.KeyAction.DESTROYED);
        keyLog.setTimestamp(System.currentTimeMillis());
        this.logKeyAction(keyLog);

        return true;
    }

    public void logKeyPickup(final org.bukkit.entity.Player player, final org.bukkit.inventory.ItemStack item) {
        if (player == null || item == null || !this.isKeyItem(item)) {
            return;
        }

        final String chestId = this.getKeyChestId(item);
        final UUID keyId = this.getKeyId(item);

        final KeyLog keyLog = new KeyLog();
        keyLog.setKeyId(keyId);
        keyLog.setChestId(chestId);
        final ChestLock chest = chestId != null ? this.chestStorage.getChest(chestId) : null;
        keyLog.setOwnerUuid(chest != null ? chest.getOwnerUuid() : null);
        keyLog.setType(KeyLog.KeyType.PHYSICAL);
        keyLog.setLocation(player.getLocation() != null ?
            player.getLocation().getWorld().getName() + "," + (int) player.getLocation().getX() + "," +
                (int) player.getLocation().getY() + "," + (int) player.getLocation().getZ() : "player_inventory");
        keyLog.setAction(KeyLog.KeyAction.PICKED_UP);
        keyLog.setTimestamp(System.currentTimeMillis());
        this.logKeyAction(keyLog);
    }

    public void logKeyDrop(final org.bukkit.entity.Player player, final org.bukkit.inventory.ItemStack item) {
        if (player == null || item == null || !this.isKeyItem(item)) {
            return;
        }

        final String chestId = this.getKeyChestId(item);
        final UUID keyId = this.getKeyId(item);

        final KeyLog keyLog = new KeyLog();
        keyLog.setKeyId(keyId);
        keyLog.setChestId(chestId);
        final ChestLock chest = chestId != null ? this.chestStorage.getChest(chestId) : null;
        keyLog.setOwnerUuid(chest != null ? chest.getOwnerUuid() : null);
        keyLog.setType(KeyLog.KeyType.PHYSICAL);
        keyLog.setLocation(player.getLocation() != null ?
            player.getLocation().getWorld().getName() + "," + (int) player.getLocation().getX() + "," +
                (int) player.getLocation().getY() + "," + (int) player.getLocation().getZ() : "player_inventory");
        keyLog.setAction(KeyLog.KeyAction.DROPPED);
        keyLog.setTimestamp(System.currentTimeMillis());
        this.logKeyAction(keyLog);
    }

    public void logChestAction(final org.bukkit.Location location, final String chestId, final ChestLog.ChestAction action) {
        if (location == null) {
            return;
        }

        if (!this.isGlobalLogging()) {
            return;
        }

        final ChestLog log = new ChestLog();
        log.setChestId(chestId);
        log.setWorldName(location.getWorld().getName());
        log.setX(location.getBlockX());
        log.setY(location.getBlockY());
        log.setZ(location.getBlockZ());
        if (chestId != null) {
            final ChestLock chest = this.chestStorage.getChest(chestId);
            if (chest != null) {
                log.setOwnerUuid(chest.getOwnerUuid());
            }
        }
        log.setAction(action);
        log.setTimestamp(System.currentTimeMillis());
        this.chestLogStorage.logChestAction(log);
    }

    public void logChestPlace(final org.bukkit.Location location, final String chestId) {
        this.logChestAction(location, chestId, ChestLog.ChestAction.PLACE);
    }

    public void logChestBreak(final org.bukkit.Location location, final String chestId) {
        this.logChestAction(location, chestId, ChestLog.ChestAction.BREAK);
    }

    private void logKeyAction(final KeyLog log) {
    }

    public List<ChestLog> getRecentChestLogs(final int limit) {
        return this.chestLogStorage.getRecentLogs(limit);
    }

    public List<ChestLog> getChestLogs(final String chestId, final int limit) {
        return this.chestLogStorage.getLogsForChest(chestId, limit);
    }
}
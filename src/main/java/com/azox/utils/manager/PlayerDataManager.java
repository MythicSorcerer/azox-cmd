package com.azox.utils.manager;

import com.azox.utils.AzoxUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerDataManager {

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    private final File dataFolder;
    private final Map<UUID, FileConfiguration> configs = new HashMap<>();

    public PlayerDataManager() {
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public FileConfiguration getConfig(final org.bukkit.OfflinePlayer player) {
        if (player == null || player.getUniqueId() == null) return null;
        return configs.computeIfAbsent(player.getUniqueId(), k -> loadConfig(player.getUniqueId(), player.getName()));
    }

    public FileConfiguration getConfig(final Player player) {
        return configs.computeIfAbsent(player.getUniqueId(), k -> loadConfig(player));
    }

    public FileConfiguration getConfig(final UUID uuid, final String name) {
        return configs.computeIfAbsent(uuid, k -> loadConfig(uuid, name));
    }

    private FileConfiguration loadConfig(final Player player) {
        return loadConfig(player.getUniqueId(), player.getName());
    }

    private FileConfiguration loadConfig(final UUID uuid, final String name) {
        final File file = getFile(uuid, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig(final UUID uuid, final String name) {
        final FileConfiguration config = configs.get(uuid);
        if (config == null) return;
        try {
            config.save(getFile(uuid, name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFile(final UUID uuid, final String name) {
        return new File(dataFolder, name + "_" + uuid.toString() + ".yml");
    }

    public void unload(final UUID uuid) {
        configs.remove(uuid);
    }
}

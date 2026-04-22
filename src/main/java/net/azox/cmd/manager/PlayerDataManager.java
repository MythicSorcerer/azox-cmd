package net.azox.cmd.manager;

import net.azox.cmd.AzoxCmd;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerDataManager {

    private final AzoxCmd plugin = AzoxCmd.getInstance();
    private final File dataFolder;
    private final Map<UUID, FileConfiguration> configs;

    public PlayerDataManager() {
        this.dataFolder = new File(this.plugin.getDataFolder(), "playerdata");
        this.configs = new HashMap<>();
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
    }

    public FileConfiguration getConfig(final org.bukkit.OfflinePlayer player) {
        if (player == null || player.getUniqueId() == null) {
            return null;
        }
        return this.configs.computeIfAbsent(player.getUniqueId(), key -> this.loadConfig(player.getUniqueId(), player.getName()));
    }

    public FileConfiguration getConfig(final Player player) {
        if (player == null) {
            return null;
        }
        return this.configs.computeIfAbsent(player.getUniqueId(), key -> this.loadConfig(player));
    }

    public FileConfiguration getConfig(final UUID uuid, final String name) {
        if (uuid == null) {
            return null;
        }
        return this.configs.computeIfAbsent(uuid, key -> this.loadConfig(uuid, name));
    }

    private FileConfiguration loadConfig(final Player player) {
        if (player == null) {
            return null;
        }
        return this.loadConfig(player.getUniqueId(), player.getName());
    }

    private FileConfiguration loadConfig(final UUID uuid, final String name) {
        if (uuid == null) {
            return null;
        }
        final File file = this.getFile(uuid, name);
        if (file == null || !file.exists()) {
            try {
                if (file != null && !file.createNewFile()) {
                    this.plugin.getLogger().warning("Failed to create config file for: " + name);
                }
            } catch (final IOException exception) {
                this.plugin.getLogger().severe("Failed to create config file for: " + name);
                exception.printStackTrace();
            }
        }
        return file != null ? YamlConfiguration.loadConfiguration(file) : null;
    }

    public void saveConfig(final UUID uuid, final String name) {
        if (uuid == null) {
            return;
        }
        final FileConfiguration config = this.configs.get(uuid);
        if (config == null) {
            return;
        }
        final File file = this.getFile(uuid, name);
        if (file == null) {
            return;
        }
        try {
            config.save(file);
        } catch (final IOException exception) {
            this.plugin.getLogger().severe("Failed to save config for: " + name);
            exception.printStackTrace();
        }
    }

    private File getFile(final UUID uuid, final String name) {
        if (uuid == null || name == null) {
            return null;
        }
        return new File(this.dataFolder, name + "_" + uuid.toString() + ".yml");
    }

    public void unload(final UUID uuid) {
        if (uuid == null) {
            return;
        }
        this.configs.remove(uuid);
    }
}

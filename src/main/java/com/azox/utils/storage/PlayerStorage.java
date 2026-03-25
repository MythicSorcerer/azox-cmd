package com.azox.utils.storage;

import com.azox.utils.AzoxUtils;
import com.azox.utils.model.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PlayerStorage {

    private final AzoxUtils plugin = AzoxUtils.getInstance();

    private FileConfiguration getConfig(final OfflinePlayer player) {
        if (player == null) {
            return null;
        }
        return plugin.getPlayerDataManager().getConfig(player);
    }

    private void save(final OfflinePlayer player) {
        if (player == null) {
            return;
        }
        plugin.getPlayerDataManager().saveConfig(player.getUniqueId(), player.getName());
    }

    public void saveHome(final OfflinePlayer player, final Home home) {
        if (player == null || home == null) {
            return;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return;
        }
        final String path = "homes." + home.getName().toLowerCase();
        config.set(path + ".name", home.getName());
        config.set(path + ".world", home.getWorldName());
        config.set(path + ".x", home.getX());
        config.set(path + ".y", home.getY());
        config.set(path + ".z", home.getZ());
        config.set(path + ".yaw", home.getYaw());
        config.set(path + ".pitch", home.getPitch());
        config.set(path + ".isPublic", home.isPublic());
        config.set(path + ".description", home.getDescription());
        config.set(path + ".creationDate", home.getCreationDate());
        save(player);
    }

    public void deleteHome(final OfflinePlayer player, final String name) {
        if (player == null || name == null) {
            return;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return;
        }
        config.set("homes." + name.toLowerCase(), null);
        save(player);
    }

    public Map<String, Home> getHomes(final OfflinePlayer player) {
        final Map<String, Home> homes = new HashMap<>();
        if (player == null) {
            return homes;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return homes;
        }
        final ConfigurationSection section = config.getConfigurationSection("homes");
        if (section == null) {
            return homes;
        }

        for (final String key : section.getKeys(false)) {
            final ConfigurationSection homeSection = section.getConfigurationSection(key);
            if (homeSection == null) {
                continue;
            }
            homes.put(key, new Home(
                    player.getUniqueId(),
                    homeSection.getString("name"),
                    homeSection.getString("world"),
                    homeSection.getDouble("x"),
                    homeSection.getDouble("y"),
                    homeSection.getDouble("z"),
                    (float) homeSection.getDouble("yaw"),
                    (float) homeSection.getDouble("pitch"),
                    homeSection.getBoolean("isPublic"),
                    homeSection.getString("description"),
                    homeSection.getLong("creationDate")
            ));
        }
        return homes;
    }

    public List<Home> getPublicHomes() {
        final List<Home> publicHomes = new ArrayList<>();
        final File dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            return publicHomes;
        }

        final File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return publicHomes;
        }

        for (final File file : files) {
            final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            final ConfigurationSection homesSection = config.getConfigurationSection("homes");
            if (homesSection == null) {
                continue;
            }

            final String fileName = file.getName();
            final String uuidStr = fileName.substring(fileName.lastIndexOf('_') + 1, fileName.lastIndexOf('.'));
            final UUID uuid;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (final IllegalArgumentException exception) {
                continue;
            }

            for (final String key : homesSection.getKeys(false)) {
                final ConfigurationSection homeSection = homesSection.getConfigurationSection(key);
                if (homeSection != null && homeSection.getBoolean("isPublic")) {
                    publicHomes.add(new Home(
                            uuid,
                            key,
                            homeSection.getString("world"),
                            homeSection.getDouble("x"),
                            homeSection.getDouble("y"),
                            homeSection.getDouble("z"),
                            (float) homeSection.getDouble("yaw"),
                            (float) homeSection.getDouble("pitch"),
                            true,
                            homeSection.getString("description"),
                            homeSection.getLong("creationDate")
                    ));
                }
            }
        }
        return publicHomes;
    }

    public void setBackLocation(final OfflinePlayer player, final Location location) {
        if (player == null) {
            return;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return;
        }
        if (location == null) {
            config.set("back", null);
        } else {
            config.set("back.world", location.getWorld() != null ? location.getWorld().getName() : null);
            config.set("back.x", Math.round(location.getX() * 100.0) / 100.0);
            config.set("back.y", Math.round(location.getY() * 100.0) / 100.0);
            config.set("back.z", Math.round(location.getZ() * 100.0) / 100.0);
            config.set("back.yaw", Math.round(location.getYaw() * 100.0) / 100.0);
            config.set("back.pitch", Math.round(location.getPitch() * 100.0) / 100.0);
        }
        save(player);
    }

    public Location getBackLocation(final OfflinePlayer player) {
        if (player == null) {
            return null;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return null;
        }
        final ConfigurationSection section = config.getConfigurationSection("back");
        if (section == null) {
            return null;
        }
        final String worldName = section.getString("world", "");
        if (worldName == null || worldName.isEmpty()) {
            return null;
        }
        final World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"), (float) section.getDouble("yaw"), (float) section.getDouble("pitch"));
    }

    public boolean isGuiEnabled(final OfflinePlayer player) {
        if (player == null) {
            return true;
        }
        return getConfig(player).getBoolean("prefs.gui", true);
    }

    public void setGuiEnabled(final OfflinePlayer player, final boolean enabled) {
        if (player == null) {
            return;
        }
        getConfig(player).set("prefs.gui", enabled);
        save(player);
    }

    public boolean areParticlesEnabled(final OfflinePlayer player) {
        if (player == null) {
            return true;
        }
        return getConfig(player).getBoolean("prefs.particles", true);
    }

    public void setParticlesEnabled(final OfflinePlayer player, final boolean enabled) {
        if (player == null) {
            return;
        }
        getConfig(player).set("prefs.particles", enabled);
        save(player);
    }

    public boolean isVanishFakeMessages(final OfflinePlayer player) {
        if (player == null) {
            return true;
        }
        return getConfig(player).getBoolean("prefs.vanish.fake_messages", true);
    }

    public void setVanishFakeMessages(final OfflinePlayer player, final boolean enabled) {
        if (player == null) {
            return;
        }
        getConfig(player).set("prefs.vanish.fake_messages", enabled);
        save(player);
    }

    public boolean isVanishAutoFly(final OfflinePlayer player) {
        if (player == null) {
            return true;
        }
        return getConfig(player).getBoolean("prefs.vanish.auto_fly", true);
    }

    public void setVanishAutoFly(final OfflinePlayer player, final boolean enabled) {
        if (player == null) {
            return;
        }
        getConfig(player).set("prefs.vanish.auto_fly", enabled);
        save(player);
    }

    public boolean isVanishAutoGod(final OfflinePlayer player) {
        if (player == null) {
            return true;
        }
        return getConfig(player).getBoolean("prefs.vanish.auto_god", true);
    }

    public void setVanishAutoGod(final OfflinePlayer player, final boolean enabled) {
        if (player == null) {
            return;
        }
        getConfig(player).set("prefs.vanish.auto_god", enabled);
        save(player);
    }

    public boolean isVanishPickupDisabled(final OfflinePlayer player) {
        if (player == null) {
            return true;
        }
        return getConfig(player).getBoolean("prefs.vanish.pickup_disabled", true);
    }

    public void setVanishPickupDisabled(final OfflinePlayer player, final boolean disabled) {
        if (player == null) {
            return;
        }
        getConfig(player).set("prefs.vanish.pickup_disabled", disabled);
        save(player);
    }

    public boolean isGodMobsIgnore(final OfflinePlayer player) {
        if (player == null) {
            return true;
        }
        return getConfig(player).getBoolean("prefs.god.mobs_ignore", true);
    }

    public void setGodMobsIgnore(final OfflinePlayer player, final boolean enabled) {
        if (player == null) {
            return;
        }
        getConfig(player).set("prefs.god.mobs_ignore", enabled);
        save(player);
    }

    public boolean isTpIgnore(final OfflinePlayer player) {
        if (player == null) {
            return false;
        }
        return getConfig(player).getBoolean("prefs.tp_ignore", false);
    }

    public void setTpIgnore(final OfflinePlayer player, final boolean enabled) {
        if (player == null) {
            return;
        }
        getConfig(player).set("prefs.tp_ignore", enabled);
        save(player);
    }

    public boolean isNightVisionEnabled(final OfflinePlayer player) {
        if (player == null) {
            return false;
        }
        return getConfig(player).getBoolean("prefs.night_vision", false);
    }

    public void setNightVisionEnabled(final OfflinePlayer player, final boolean enabled) {
        if (player == null) {
            return;
        }
        getConfig(player).set("prefs.night_vision", enabled);
        save(player);
    }

    public void setJailed(final OfflinePlayer player, final String name, final boolean inescapable) {
        if (player == null) {
            return;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return;
        }
        config.set("jail.name", name);
        config.set("jail.inescapable", inescapable);
        save(player);
    }

    public void setUnjailed(final OfflinePlayer player) {
        if (player == null) {
            return;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return;
        }
        config.set("jail", null);
        save(player);
    }

    public String getJailName(final OfflinePlayer player) {
        if (player == null) {
            return null;
        }
        final FileConfiguration config = getConfig(player);
        return config != null ? config.getString("jail.name") : null;
    }

    public boolean isJailInescapable(final OfflinePlayer player) {
        if (player == null) {
            return false;
        }
        final FileConfiguration config = getConfig(player);
        return config != null && config.getBoolean("jail.inescapable", false);
    }

    public void saveEnderChestPage(final OfflinePlayer player, final int page, final ItemStack[] items) {
        if (player == null || items == null) {
            return;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return;
        }
        config.set("enderchest.page_" + page, items);
        save(player);
    }

    public ItemStack[] getEnderChestPage(final OfflinePlayer player, final int page) {
        if (player == null) {
            return new ItemStack[27];
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return new ItemStack[27];
        }
        final List<?> list = config.getList("enderchest.page_" + page);
        if (list == null) {
            return new ItemStack[27];
        }
        return list.toArray(new ItemStack[0]);
    }
}

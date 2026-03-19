package com.azox.utils.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public final class JailStorage extends BaseStorage {

    public JailStorage() {
        super("jails.yml");
    }

    public void saveJail(final String name, final Location location) {
        final String path = name.toLowerCase();
        this.config.set(path + ".world", location.getWorld().getName());
        this.config.set(path + ".x", location.getX());
        this.config.set(path + ".y", location.getY());
        this.config.set(path + ".z", location.getZ());
        this.config.set(path + ".yaw", location.getYaw());
        this.config.set(path + ".pitch", location.getPitch());
        this.save();
    }

    public void deleteJail(final String name) {
        this.config.set(name.toLowerCase(), null);
        this.save();
    }

    public Map<String, Location> getJails() {
        final Map<String, Location> jails = new HashMap<>();
        for (final String key : this.config.getKeys(false)) {
            final ConfigurationSection section = this.config.getConfigurationSection(key);
            if (section == null) continue;

            final World world = Bukkit.getWorld(section.getString("world", ""));
            if (world == null) continue;

            final Location loc = new Location(
                    world,
                    section.getDouble("x"),
                    section.getDouble("y"),
                    section.getDouble("z"),
                    (float) section.getDouble("yaw"),
                    (float) section.getDouble("pitch")
            );
            jails.put(key, loc);
        }
        return jails;
    }
}

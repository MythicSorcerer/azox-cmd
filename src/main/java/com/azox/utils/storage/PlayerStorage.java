package com.azox.utils.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public final class PlayerStorage extends BaseStorage {

    public PlayerStorage() {
        super("players.yml");
    }

    public void setBackLocation(final UUID uuid, final Location location) {
        if (location == null) {
            this.config.set(uuid.toString() + ".back", null);
        } else {
            final String path = uuid.toString() + ".back";
            this.config.set(path + ".world", location.getWorld().getName());
            this.config.set(path + ".x", location.getX());
            this.config.set(path + ".y", location.getY());
            this.config.set(path + ".z", location.getZ());
            this.config.set(path + ".yaw", location.getYaw());
            this.config.set(path + ".pitch", location.getPitch());
        }
        this.save();
    }

    public Location getBackLocation(final UUID uuid) {
        final ConfigurationSection section = this.config.getConfigurationSection(uuid.toString() + ".back");
        if (section == null) return null;

        final World world = Bukkit.getWorld(section.getString("world", ""));
        if (world == null) return null;

        return new Location(
                world,
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch")
        );
    }

    public void setTpIgnore(final UUID uuid, final boolean ignore) {
        this.config.set(uuid.toString() + ".tpignore", ignore);
        this.save();
    }

    public boolean isTpIgnore(final UUID uuid) {
        return this.config.getBoolean(uuid.toString() + ".tpignore", false);
    }

    public void setGuiEnabled(final UUID uuid, final boolean enabled) {
        this.config.set(uuid.toString() + ".gui", enabled);
        this.save();
    }

    public boolean isGuiEnabled(final UUID uuid) {
        return this.config.getBoolean(uuid.toString() + ".gui", true);
    }

    // Vanish Preferences
    public void setVanishFakeMessages(final UUID uuid, final boolean enabled) {
        this.config.set(uuid.toString() + ".vanish.fake_messages", enabled);
        this.save();
    }

    public boolean isVanishFakeMessages(final UUID uuid) {
        return this.config.getBoolean(uuid.toString() + ".vanish.fake_messages", true);
    }

    public void setVanishAutoFly(final UUID uuid, final boolean enabled) {
        this.config.set(uuid.toString() + ".vanish.auto_fly", enabled);
        this.save();
    }

    public boolean isVanishAutoFly(final UUID uuid) {
        return this.config.getBoolean(uuid.toString() + ".vanish.auto_fly", true);
    }

    public void setVanishAutoGod(final UUID uuid, final boolean enabled) {
        this.config.set(uuid.toString() + ".vanish.auto_god", enabled);
        this.save();
    }

    public boolean isVanishAutoGod(final UUID uuid) {
        return this.config.getBoolean(uuid.toString() + ".vanish.auto_god", true);
    }

    public void setVanishPickupDisabled(final UUID uuid, final boolean disabled) {
        this.config.set(uuid.toString() + ".vanish.pickup_disabled", disabled);
        this.save();
    }

    public boolean isVanishPickupDisabled(final UUID uuid) {
        return this.config.getBoolean(uuid.toString() + ".vanish.pickup_disabled", true);
    }

    // God Mode Preferences
    public void setGodMobsIgnore(final UUID uuid, final boolean ignore) {
        this.config.set(uuid.toString() + ".god.mobs_ignore", ignore);
        this.save();
    }

    public boolean isGodMobsIgnore(final UUID uuid) {
        return this.config.getBoolean(uuid.toString() + ".god.mobs_ignore", true);
    }

    public void setJailed(final UUID uuid, final String jailName, final boolean inescapable) {
        this.config.set(uuid.toString() + ".jail.name", jailName);
        this.config.set(uuid.toString() + ".jail.inescapable", inescapable);
        this.save();
    }

    public void setUnjailed(final UUID uuid) {
        this.config.set(uuid.toString() + ".jail", null);
        this.save();
    }

    public String getJailName(final UUID uuid) {
        return this.config.getString(uuid.toString() + ".jail.name");
    }

    public boolean isJailInescapable(final UUID uuid) {
        return this.config.getBoolean(uuid.toString() + ".jail.inescapable", false);
    }
}

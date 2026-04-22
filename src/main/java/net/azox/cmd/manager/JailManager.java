package net.azox.cmd.manager;

import net.azox.cmd.AzoxCmd;
import lombok.Getter;
import net.azox.cmd.storage.JailStorage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class JailManager {

    private final AzoxCmd plugin = AzoxCmd.getInstance();
    @Getter
    private final JailStorage storage;
    private final Map<String, Location> cachedJails;

    public JailManager() {
        this.storage = new JailStorage();
        this.cachedJails = new ConcurrentHashMap<>(this.storage.getJails());
    }

    public Optional<Location> getJail(final String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.cachedJails.get(name.toLowerCase()));
    }

    public void setJail(final String name, final Location location) {
        if (name == null || location == null) {
            return;
        }
        this.cachedJails.put(name.toLowerCase(), location);
        this.storage.saveJail(name, location);
    }

    public void deleteJail(final String name) {
        if (name == null) {
            return;
        }
        this.cachedJails.remove(name.toLowerCase());
        this.storage.deleteJail(name);
    }

    public void jailPlayer(final Player player, final String jailName, final boolean inescapable) {
        if (player == null || jailName == null) {
            return;
        }
        this.getJail(jailName).ifPresent(player::teleport);
        this.plugin.getPlayerStorage().setJailed(player, jailName, inescapable);
    }

    public void unjailPlayer(final Player player) {
        if (player == null) {
            return;
        }
        this.plugin.getPlayerStorage().setUnjailed(player);
    }

    public Map<String, Location> getJails() {
        return this.cachedJails;
    }
}

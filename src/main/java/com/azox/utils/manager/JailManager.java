package com.azox.utils.manager;

import com.azox.utils.storage.JailStorage;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class JailManager {

    @Getter
    private final JailStorage storage;
    private final Map<String, Location> cachedJails;

    public JailManager() {
        this.storage = new JailStorage();
        this.cachedJails = new ConcurrentHashMap<>(storage.getJails());
    }

    public Optional<Location> getJail(final String name) {
        return Optional.ofNullable(cachedJails.get(name.toLowerCase()));
    }

    public void setJail(final String name, final Location location) {
        cachedJails.put(name.toLowerCase(), location);
        storage.saveJail(name, location);
    }

    public void deleteJail(final String name) {
        cachedJails.remove(name.toLowerCase());
        storage.deleteJail(name);
    }

    public void jailPlayer(final Player player, final String jailName, final boolean inescapable) {
        // Implementation of teleport and logic
        // This will be called from command
    }

    public Map<String, Location> getJails() {
        return cachedJails;
    }
}

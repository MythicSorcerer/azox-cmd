package com.azox.utils.manager;

import com.azox.utils.AzoxUtils;
import com.azox.utils.model.Home;
import com.azox.utils.storage.HomeStorage;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class HomeManager {

    @Getter
    private final HomeStorage storage;
    private final Map<UUID, Map<String, Home>> cachedHomes = new ConcurrentHashMap<>();

    public HomeManager() {
        this.storage = new HomeStorage();
    }

    public Map<String, Home> getHomes(final UUID uuid) {
        return cachedHomes.computeIfAbsent(uuid, k -> storage.getHomes(k));
    }

    public Optional<Home> getHome(final UUID uuid, final String name) {
        return Optional.ofNullable(getHomes(uuid).get(name.toLowerCase()));
    }

    public void setHome(final Player player, final String name, final Location location) {
        final UUID uuid = player.getUniqueId();
        final Map<String, Home> homes = getHomes(uuid);
        
        final String homeName = name.toLowerCase();
        final Home home = homes.getOrDefault(homeName, new Home());
        home.setOwnerUuid(uuid);
        home.setName(name); // preserve casing for display if we want, but key is lower
        home.setLocation(location);
        if (home.getCreationDate() == 0) {
            home.setCreationDate(System.currentTimeMillis());
            home.setPublic(false);
            home.setDescription("");
        }
        
        homes.put(homeName, home);
        storage.saveHome(home);
    }

    public void deleteHome(final UUID uuid, final String name) {
        final Map<String, Home> homes = getHomes(uuid);
        homes.remove(name.toLowerCase());
        storage.deleteHome(uuid, name.toLowerCase());
    }

    public void deleteAllHomes(final UUID uuid) {
        cachedHomes.remove(uuid);
        storage.deleteAllHomes(uuid);
    }

    public int getHomeLimit(final Player player) {
        if (player.hasPermission("azox.utils.sethome.unlimited")) {
            return Integer.MAX_VALUE;
        }
        
        // This is where we hook into azox-ranks
        // For now, let's use permissions like azox.utils.homes.5
        int limit = 4;
        for (int i = 100; i > limit; i--) {
            if (player.hasPermission("azox.utils.homes." + i)) {
                return i;
            }
        }
        return limit;
    }

    public List<Home> getPublicHomes() {
        return storage.getPublicHomes();
    }
}

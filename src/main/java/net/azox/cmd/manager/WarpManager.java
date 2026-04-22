package net.azox.cmd.manager;

import lombok.Getter;
import net.azox.cmd.model.Warp;
import net.azox.cmd.storage.WarpStorage;
import org.bukkit.Location;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class WarpManager {

    @Getter
    private final WarpStorage storage;
    private final Map<String, Warp> cachedWarps;

    public WarpManager() {
        this.storage = new WarpStorage();
        this.cachedWarps = new ConcurrentHashMap<>(this.storage.getWarps());
    }

    public Optional<Warp> getWarp(final String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.cachedWarps.get(name.toLowerCase()));
    }

    public void setWarp(final String name, final Location location, final int level) {
        if (name == null || location == null) {
            return;
        }
        final String warpName = name.toLowerCase();
        final Warp warp = this.cachedWarps.getOrDefault(warpName, new Warp());
        warp.setName(name);
        warp.setLocation(location);
        warp.setLevel(level);

        this.cachedWarps.put(warpName, warp);
        this.storage.saveWarp(warp);
    }

    public void deleteWarp(final String name) {
        if (name == null) {
            return;
        }
        this.cachedWarps.remove(name.toLowerCase());
        this.storage.deleteWarp(name.toLowerCase());
    }

    public Map<String, Warp> getWarps() {
        return this.cachedWarps;
    }
}

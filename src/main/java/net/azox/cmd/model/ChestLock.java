package net.azox.cmd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class ChestLock {
    private UUID ownerUuid;
    private String chestId;
    private String worldName;
    private int x;
    private int y;
    private int z;
    private List<UUID> allowedPlayers;
    private long creationDate;
    private boolean locked;

    public Location getLocation() {
        if (this.worldName == null) {
            return null;
        }
        final World world = Bukkit.getWorld(this.worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, this.x, this.y, this.z);
    }

    public void setLocation(final Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        this.worldName = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public boolean isAllowed(final UUID playerUuid) {
        if (this.ownerUuid.equals(playerUuid)) {
            return true;
        }
        return this.allowedPlayers != null && this.allowedPlayers.contains(playerUuid);
    }

    public void addAllowedPlayer(final UUID playerUuid) {
        if (this.allowedPlayers == null) {
            this.allowedPlayers = new ArrayList<>();
        }
        if (!this.allowedPlayers.contains(playerUuid)) {
            this.allowedPlayers.add(playerUuid);
        }
    }

    public void removeAllowedPlayer(final UUID playerUuid) {
        if (this.allowedPlayers != null) {
            this.allowedPlayers.remove(playerUuid);
        }
    }
}
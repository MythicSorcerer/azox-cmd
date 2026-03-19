package com.azox.utils.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class Home {
    private UUID ownerUuid;
    private String name;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean isPublic;
    private String description;
    private long creationDate;

    public Location getLocation() {
        final World world = Bukkit.getWorld(this.worldName);
        if (world == null) return null;
        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public void setLocation(final Location location) {
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }
}

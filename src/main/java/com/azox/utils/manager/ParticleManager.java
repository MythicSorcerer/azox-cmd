package com.azox.utils.manager;

import com.azox.utils.AzoxUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ParticleManager {

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    private final Map<UUID, Particle> activeParticles = new HashMap<>();

    public ParticleManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!plugin.getPlayerStorage().areParticlesEnabled(player)) continue;
                    
                    Particle particle = getPlayerParticle(player);
                    if (particle != null) {
                        player.getWorld().spawnParticle(particle, player.getLocation().add(0, 3.2, 0), 1, 0.1, 0.1, 0.1, 0.05);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    private Particle getPlayerParticle(Player player) {
        for (Particle p : Particle.values()) {
            if (player.hasPermission("azox.utils.particles." + p.name().toLowerCase())) {
                return p;
            }
        }
        return null;
    }
}

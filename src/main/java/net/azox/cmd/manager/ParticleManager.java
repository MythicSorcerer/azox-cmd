package net.azox.cmd.manager;

import net.azox.cmd.AzoxCmd;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ParticleManager {

    private final AzoxCmd plugin = AzoxCmd.getInstance();
    private final Map<UUID, Particle> activeParticles;

    public ParticleManager() {
        this.activeParticles = new HashMap<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    if (player == null) {
                        continue;
                    }
                    if (!ParticleManager.this.plugin.getPlayerStorage().areParticlesEnabled(player)) {
                        continue;
                    }

                    final Particle particle = ParticleManager.this.getPlayerParticle(player);
                    if (particle != null) {
                        // Use particles that don't require data
                        player.getWorld().spawnParticle(particle, player.getLocation().add(0, 3.2, 0), 1, 0.1, 0.1, 0.1, 0.0);
                    }
                }
            }
        }.runTaskTimer(this.plugin, 0L, 20L);
    }

    public void setParticle(final UUID uuid, final Particle particle) {
        if (uuid == null) {
            return;
        }
        if (particle == null) {
            this.activeParticles.remove(uuid);
        } else {
            this.activeParticles.put(uuid, particle);
        }
    }

    public Particle getPlayerParticle(final Player player) {
        if (player == null) {
            return null;
        }
        for (int level = 100; level > 0; level--) {
            if (player.hasPermission("azox.util.particles." + level)) {
                return this.getParticleForLevel(level);
            }
        }
        return null;
    }

    private Particle getParticleForLevel(final int level) {
        // Use only particles that don't require data
        if (level >= 100) {
            return Particle.END_ROD;
        } else if (level >= 75) {
            return Particle.HAPPY_VILLAGER;
        } else if (level >= 50) {
            return Particle.FLAME;
        } else if (level >= 25) {
            return Particle.HEART;
        }
        return Particle.SMOKE;
    }

    public boolean hasParticle(final UUID uuid) {
        return uuid != null && this.activeParticles.containsKey(uuid);
    }

    public void removeParticle(final UUID uuid) {
        if (uuid == null) {
            return;
        }
        this.activeParticles.remove(uuid);
    }
}

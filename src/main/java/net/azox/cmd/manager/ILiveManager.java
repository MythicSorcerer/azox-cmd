package net.azox.cmd.manager;

import net.azox.cmd.AzoxCmd;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ILiveManager {

    private static final Set<Material> HEALING_FOODS = new HashSet<>(Arrays.asList(
            Material.COOKED_COD,
            Material.BREAD,
            Material.BAKED_POTATO,
            Material.COOKED_MUTTON,
            Material.COOKED_SALMON,
            Material.COOKED_CHICKEN,
            Material.PUMPKIN_PIE,
            Material.COOKED_PORKCHOP,
            Material.COOKED_BEEF,
            Material.GOLDEN_CARROT,
            Material.GOLDEN_APPLE,
            Material.ENCHANTED_GOLDEN_APPLE
    ));

    private final AzoxCmd plugin = AzoxCmd.getInstance();
    private final Map<UUID, ILiveData> ilivePlayers;

    public ILiveManager() {
        this.ilivePlayers = new ConcurrentHashMap<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final UUID uuid : ILiveManager.this.ilivePlayers.keySet()) {
                    final Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        ILiveManager.this.processILivePlayer(player);
                    }
                }
            }
        }.runTaskTimer(this.plugin, 100L, 100L);
    }

    public void enableILive(final Player player, final String mode, final int damageReduction, final boolean foodHeal) {
        if (player == null) {
            return;
        }
        this.ilivePlayers.put(player.getUniqueId(), new ILiveData(mode, damageReduction, foodHeal));
        this.plugin.getPlayerStorage().setILiveEnabled(player, true);
        this.plugin.getPlayerStorage().setILiveMode(player, mode);
        this.plugin.getPlayerStorage().setILiveDamageReduction(player, damageReduction);
        this.plugin.getPlayerStorage().setILiveFoodHeal(player, foodHeal);
    }

    public void disableILive(final Player player) {
        if (player == null) {
            return;
        }
        this.ilivePlayers.remove(player.getUniqueId());
        this.plugin.getPlayerStorage().setILiveEnabled(player, false);
    }

    public boolean isEnabled(final Player player) {
        if (player == null) {
            return false;
        }
        return this.ilivePlayers.containsKey(player.getUniqueId());
    }

    public ILiveData getData(final Player player) {
        if (player == null) {
            return null;
        }
        return this.ilivePlayers.get(player.getUniqueId());
    }

    public void loadPlayerData(final Player player) {
        if (player == null) {
            return;
        }
        if (this.plugin.getPlayerStorage().isILiveEnabled(player)) {
            String mode = this.plugin.getPlayerStorage().getILiveMode(player);
            int damageReduction = this.plugin.getPlayerStorage().getILiveDamageReduction(player);
            final boolean foodHeal = this.plugin.getPlayerStorage().isILiveFoodHealEnabled(player);
            if (mode == null) {
                mode = "nototem";
            }
            if (damageReduction < 0) {
                damageReduction = 1;
            }
            this.ilivePlayers.put(player.getUniqueId(), new ILiveData(mode, damageReduction, foodHeal));
        }
    }

    private void processILivePlayer(final Player player) {
        final ILiveData data = this.getData(player);
        if (data == null) {
            return;
        }

        if (data.foodHeal && player.getHealth() < 6.0 && player.getFoodLevel() > 0) {
            this.healWithFood(player);
        }
    }

    private void healWithFood(final Player player) {
        for (int slotIndex = 0; slotIndex < player.getInventory().getSize(); slotIndex++) {
            final ItemStack item = player.getInventory().getItem(slotIndex);
            if (item != null && HEALING_FOODS.contains(item.getType())) {
                player.getInventory().setItem(slotIndex, item.getAmount() > 1
                        ? new ItemStack(item.getType(), item.getAmount() - 1)
                        : null);
                final int newFood = Math.min(20, player.getFoodLevel() + 3);
                player.setFoodLevel(newFood);
                break;
            }
        }
    }

    public double getDamageMultiplier(final Player player) {
        final ILiveData data = this.getData(player);
        if (data == null) {
            return 1.0;
        }
        final int reduction = data.damageReduction;
        return 1.0 - (reduction * 0.2);
    }

    public boolean isNoTotemMode(final Player player) {
        final ILiveData data = this.getData(player);
        return data != null && "nototem".equals(data.mode);
    }

    public static class ILiveData {
        public final String mode;
        public final int damageReduction;
        public final boolean foodHeal;

        public ILiveData(final String mode, final int damageReduction, final boolean foodHeal) {
            this.mode = mode;
            this.damageReduction = Math.max(0, Math.min(5, damageReduction));
            this.foodHeal = foodHeal;
        }
    }
}

package net.azox.cmd.manager;

import net.azox.cmd.AzoxCmd;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FillPotManager {

    private static final int REFILL_INTERVAL_TICKS = 200;

    private final AzoxCmd plugin = AzoxCmd.getInstance();
    private final Map<UUID, List<SavedPotionSlot>> savedSlots;

    public FillPotManager() {
        this.savedSlots = new ConcurrentHashMap<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final UUID uuid : FillPotManager.this.savedSlots.keySet()) {
                    final Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline() && FillPotManager.this.plugin.getPlayerStorage().isFillPotEnabled(player)) {
                        FillPotManager.this.refillPotions(player);
                    }
                }
            }
        }.runTaskTimer(this.plugin, REFILL_INTERVAL_TICKS, REFILL_INTERVAL_TICKS);
    }

    public void loadPlayerData(final Player player) {
        if (player == null) {
            return;
        }
        if (this.plugin.getPlayerStorage().hasSavedPotions(player)) {
            final List<String> savedData = this.plugin.getPlayerStorage().getSavedPotions(player);
            final List<SavedPotionSlot> slots = new ArrayList<>();
            for (final String entry : savedData) {
                final String[] parts = entry.split(":", 2);
                if (parts.length == 2) {
                    try {
                        final int slot = Integer.parseInt(parts[0]);
                        final ItemStack item = this.deserializeItemStack(parts[1]);
                        if (item != null) {
                            slots.add(new SavedPotionSlot(slot, item.clone()));
                        }
                    } catch (NumberFormatException ignored) {
                        // ignore invalid entries
                    }
                }
            }
            this.savedSlots.put(player.getUniqueId(), slots);
        }
    }

    public void unloadPlayerData(final UUID uuid) {
        if (uuid == null) {
            return;
        }
        this.savedSlots.remove(uuid);
    }

    public void savePlayerPotions(final Player player) {
        if (player == null) {
            return;
        }
        final List<SavedPotionSlot> slots = new ArrayList<>();
        for (int slotIndex = 0; slotIndex < player.getInventory().getSize(); slotIndex++) {
            final ItemStack item = player.getInventory().getItem(slotIndex);
            if (item != null && this.isPotion(item.getType())) {
                slots.add(new SavedPotionSlot(slotIndex, item.clone()));
            }
        }
        this.savedSlots.put(player.getUniqueId(), slots);
    }

    private void refillPotions(final Player player) {
        final List<SavedPotionSlot> slots = this.savedSlots.get(player.getUniqueId());
        if (slots == null) {
            return;
        }

        for (final SavedPotionSlot slot : slots) {
            final ItemStack current = player.getInventory().getItem(slot.slot);
            if (current == null || current.getAmount() < slot.item.getAmount()) {
                player.getInventory().setItem(slot.slot, slot.item.clone());
            }
        }
    }

    private boolean isPotion(final Material material) {
        return material == Material.POTION ||
               material == Material.SPLASH_POTION ||
               material == Material.LINGERING_POTION;
    }

    private ItemStack deserializeItemStack(final String data) {
        if (data == null || data.isBlank()) {
            return null;
        }
        try {
            final String[] parts = data.split(",");
            if (parts.length < 3) {
                return null;
            }
            final Material material = Material.getMaterial(parts[0]);
            if (material == null) {
                return null;
            }

            final int amount = Integer.parseInt(parts[1]);
            final short durability = Short.parseShort(parts[2]);

            final ItemStack item = new ItemStack(material, amount, durability);

            if (parts.length > 3) {
                final org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    for (int partIndex = 3; partIndex < parts.length; partIndex++) {
                        if (parts[partIndex].startsWith("name:")) {
                            meta.setDisplayName(parts[partIndex].substring(5).replace(";", ","));
                        } else if (parts[partIndex].startsWith("lore:")) {
                            final String loreStr = parts[partIndex].substring(5);
                            final List<String> lore = new ArrayList<>();
                            for (final String line : loreStr.split("\\|")) {
                                lore.add(line.replace(";", ","));
                            }
                            meta.setLore(lore);
                        }
                    }
                    item.setItemMeta(meta);
                }
            }

            return item;
        } catch (final Exception exception) {
            return null;
        }
    }

    public static class SavedPotionSlot {
        public final int slot;
        public final ItemStack item;

        public SavedPotionSlot(final int slot, final ItemStack item) {
            this.slot = slot;
            this.item = item;
        }
    }
}

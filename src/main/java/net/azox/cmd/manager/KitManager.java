package net.azox.cmd.manager;

import net.azox.cmd.model.Kit;
import net.azox.cmd.storage.KitStorage;
import lombok.Getter;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class KitManager {

    @Getter
    private final KitStorage storage;
    private final Map<String, Kit> cachedKits;
    private final Map<UUID, Map<String, Long>> kitCooldowns;

    public KitManager() {
        this.storage = new KitStorage();
        this.cachedKits = new ConcurrentHashMap<>(this.storage.getKits());
        this.kitCooldowns = new ConcurrentHashMap<>();
    }

    public Optional<Kit> getKit(final String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.cachedKits.get(name.toLowerCase()));
    }

    public void createKit(final String name, final ItemStack[] contents, final long cooldown) {
        if (name == null || contents == null) {
            return;
        }
        final Kit kit = new Kit(name.toLowerCase(), contents, cooldown);
        this.cachedKits.put(name.toLowerCase(), kit);
        this.storage.saveKit(kit);
    }

    public void deleteKit(final String name) {
        if (name == null) {
            return;
        }
        this.cachedKits.remove(name.toLowerCase());
        this.storage.deleteKit(name);
    }

    public void giveKit(final Player player, final Kit kit) {
        if (player == null || kit == null) {
            return;
        }
        if (!player.hasPermission("azox.util.kit." + kit.getName()) && !player.hasPermission("azox.util.kit.*")) {
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " You don't have permission for this kit!");
            return;
        }

        final ItemStack[] contents = kit.getContents();
        if (contents == null) {
            return;
        }

        final long now = System.currentTimeMillis();
        final Map<String, Long> playerCooldowns = this.kitCooldowns.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());
        final long lastUsed = playerCooldowns.getOrDefault(kit.getName(), 0L);
        final long cooldownMillis = kit.getCooldown() * 1000;

        if (now - lastUsed < cooldownMillis && !player.hasPermission("azox.util.kit.bypass")) {
            final long remaining = (cooldownMillis - (now - lastUsed)) / 1000;
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Kit on cooldown! Wait " + remaining + "s.");
            return;
        }

        for (final ItemStack item : contents) {
            if (item != null) {
                player.getInventory().addItem(item.clone());
            }
        }

        playerCooldowns.put(kit.getName(), now);
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Received kit " + kit.getName() + "!");
    }

    public Map<String, Kit> getKits() {
        return this.cachedKits;
    }
}

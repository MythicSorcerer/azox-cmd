package com.azox.utils.listener;

import com.azox.utils.AzoxUtils;
import com.azox.utils.manager.GuiManager;
import com.azox.utils.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class PlayerListener implements Listener {

    private final AzoxUtils plugin = AzoxUtils.getInstance();

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        plugin.getVanishManager().handleJoin(event.getPlayer());
        if (plugin.getVanishManager().isVanished(event.getPlayer().getUniqueId())) {
            event.joinMessage(null);
        }
        checkLobby(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(final PlayerChangedWorldEvent event) {
        checkLobby(event.getPlayer());
    }

    private void checkLobby(Player player) {
        String world = player.getWorld().getName().toLowerCase();
        if (world.contains("hub") || world.contains("lobby")) {
            if (!player.getInventory().contains(Material.COMPASS)) {
                ItemStack compass = new ItemStack(Material.COMPASS);
                var meta = compass.getItemMeta();
                meta.displayName(MessageUtil.parse("<green>" + MessageUtil.ICON_WARP + " World Selector"));
                meta.getPersistentDataContainer().set(GuiManager.UTILITY_KEY, PersistentDataType.STRING, "world_selector_item");
                compass.setItemMeta(meta);
                player.getInventory().setItem(4, compass); // Middle slot
            }
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        if (plugin.getVanishManager().isVanished(event.getPlayer().getUniqueId())) {
            event.quitMessage(null);
        }
    }

    @EventHandler
    public void onPickup(final EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player && !plugin.getVanishManager().canPickup(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTarget(final EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player)) return;
        Player player = (Player) event.getTarget();
        
        if (plugin.getVanishManager().isVanished(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        
        if (player.isInvulnerable() && plugin.getTeleportManager().getStorage().isGodMobsIgnore(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if (plugin.getFreezeManager().isFrozen(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(event.getPlayer(), "<red>You are frozen and cannot interact!");
            return;
        }
        
        if (event.getItem() != null && event.getItem().getType() == Material.COMPASS) {
            String type = event.getItem().getItemMeta().getPersistentDataContainer().get(GuiManager.UTILITY_KEY, PersistentDataType.STRING);
            if ("world_selector_item".equals(type)) {
                plugin.getGuiManager().openWorldSelectorGui(event.getPlayer());
            }
        }
    }
}

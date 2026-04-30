package net.azox.cmd.listener;

import net.azox.cmd.AzoxCmd;
import net.azox.cmd.manager.LockChestManager;
import net.azox.cmd.model.ChestLock;
import net.azox.cmd.model.ChestLog;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ChestListener implements Listener {

    private final AzoxCmd plugin = AzoxCmd.getInstance();
    private final LockChestManager lockChestManager;

    public ChestListener() {
        this.lockChestManager = this.plugin.getLockChestManager();
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.getBlock().getState() instanceof Chest) {
            this.lockChestManager.logChestAction(event.getBlock().getLocation(), null, ChestLog.ChestAction.PLACE);
        }
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Chest)) {
            return;
        }

        final var location = event.getBlock().getLocation();
        final var chest = this.lockChestManager.getChestAt(location);
        
        if (chest != null && !chest.getOwnerUuid().equals(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(event.getPlayer(), "<red>You cannot break a locked chest!");
            return;
        }
        
        this.lockChestManager.logChestAction(location, chest != null ? chest.getChestId() : null, ChestLog.ChestAction.BREAK);
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof org.bukkit.entity.Player player)) {
            return;
        }
        if (!(event.getInventory().getHolder() instanceof Chest chest)) {
            return;
        }
        
        final var location = chest.getLocation();
        final var lockChest = this.lockChestManager.getChestAt(location);
        
        if (lockChest != null && !lockChest.getOwnerUuid().equals(player.getUniqueId())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, "<red>This chest is locked!");
            return;
        }
        
        if (lockChest != null) {
            this.lockChestManager.logChestAction(location, lockChest.getChestId(), ChestLog.ChestAction.TAKE_ITEM);
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getClickedBlock().getState() instanceof Chest) {
            final var player = event.getPlayer();
            final var location = event.getClickedBlock().getLocation();
            final var chest = this.lockChestManager.getChestAt(location);
            
            if (chest != null && !chest.getOwnerUuid().equals(player.getUniqueId())) {
                event.setCancelled(true);
                MessageUtil.sendMessage(player, "<red>This chest is locked!");
                return;
            }
            
            if (chest != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                this.lockChestManager.logChestAction(location, chest.getChestId(), ChestLog.ChestAction.TAKE_ITEM);
            }
        }
    }
}
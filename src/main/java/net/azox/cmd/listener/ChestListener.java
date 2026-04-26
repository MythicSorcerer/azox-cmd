package net.azox.cmd.listener;

import net.azox.cmd.AzoxCmd;
import net.azox.cmd.manager.LockChestManager;
import net.azox.cmd.model.ChestLock;
import net.azox.cmd.model.ChestLog;
import net.azox.cmd.util.MessageUtil;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public final class ChestListener implements Listener {

    private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();
    private static final Set<Material> CONTAINER_MATERIALS = new HashSet<>();
    
    static {
        CONTAINER_MATERIALS.add(Material.CHEST);
        CONTAINER_MATERIALS.add(Material.TRAPPED_CHEST);
        CONTAINER_MATERIALS.add(Material.SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.BLACK_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.BLUE_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.BROWN_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.CYAN_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.GRAY_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.GREEN_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.LIGHT_BLUE_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.LIME_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.MAGENTA_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.ORANGE_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.PINK_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.PURPLE_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.RED_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.WHITE_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.YELLOW_SHULKER_BOX);
        CONTAINER_MATERIALS.add(Material.BARREL);
    }

    private final AzoxCmd plugin = AzoxCmd.getInstance();
    private final LockChestManager lockChestManager;

    public ChestListener() {
        this.lockChestManager = this.plugin.getLockChestManager();
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block placedBlock = event.getBlockPlaced();
        if (placedBlock == null || !CONTAINER_MATERIALS.contains(placedBlock.getType())) {
            return;
        }
        
        final var location = placedBlock.getLocation();
        final var existingChest = this.lockChestManager.getChestAt(location);
        if (existingChest != null) {
            return;
        }
        
        this.lockChestManager.logChestAction(location, null, ChestLog.ChestAction.PLACE);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block brokenBlock = event.getBlock();
        if (brokenBlock == null || !CONTAINER_MATERIALS.contains(brokenBlock.getType())) {
            return;
        }

        final var location = brokenBlock.getLocation();
        final var chest = this.lockChestManager.getChestAt(location);
        
        if (chest != null && !chest.isAllowed(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(event.getPlayer(), "<red>You cannot break a locked chest!");
            return;
        }
        
        this.lockChestManager.logChestAction(location, chest != null ? chest.getChestId() : null, ChestLog.ChestAction.BREAK);
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {
        final var inventoryHolder = event.getInventory().getHolder();
        
        if (inventoryHolder instanceof org.bukkit.entity.Player) {
            return;
        }
        
        if (inventoryHolder instanceof HopperMinecart) {
            final var minecart = (HopperMinecart) inventoryHolder;
            final var location = minecart.getLocation();
            if (location != null) {
                final var chest = this.lockChestManager.getChestAt(location);
                if (chest != null && !chest.isAllowed(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    MessageUtil.sendMessage(event.getPlayer(), "<red>Hopper minecart cannot access this chest!");
                    return;
                }
            }
            return;
        }
        
        if (!(inventoryHolder instanceof Chest chest)) {
            if (inventoryHolder instanceof org.bukkit.block.Block block) {
                final var location = block.getLocation();
                final var lockChest = this.lockChestManager.getChestAt(location);
                if (lockChest != null && !lockChest.isAllowed(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    MessageUtil.sendMessage(event.getPlayer(), "<red>This container is locked!");
                    return;
                }
            }
            return;
        }
        
        final var location = chest.getLocation();
        final var lockChest = this.lockChestManager.getChestAt(location);
        if (lockChest != null && !lockChest.isAllowed(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(event.getPlayer(), "<red>This chest is locked!");
            return;
        }
        
        this.lockChestManager.logChestAction(location, lockChest != null ? lockChest.getChestId() : null, ChestLog.ChestAction.TAKE_ITEM);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof org.bukkit.entity.Player player)) {
            return;
        }
        
        final var holder = event.getInventory().getHolder();
        
        if (holder instanceof Chest chest) {
            final var location = chest.getLocation();
            final var lockChest = this.lockChestManager.getChestAt(location);
            
            if (lockChest != null && !lockChest.isAllowed(player.getUniqueId())) {
                event.setCancelled(true);
                return;
            }
            
            if (lockChest != null) {
                final var currentItem = event.getCurrentItem();
                
                final var rawSlot = event.getRawSlot();
                final var topInventory = event.getView().getTopInventory();
                
                if (rawSlot < topInventory.getSize()) {
                    if (event.isShiftClick() && currentItem != null && currentItem.getType() != Material.AIR) {
                        this.lockChestManager.logChestAction(location, lockChest.getChestId(), ChestLog.ChestAction.PUT_ITEM);
                    } else if (event.isLeftClick() || event.isRightClick()) {
                        if (currentItem != null && currentItem.getType() != Material.AIR) {
                            if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
                                this.lockChestManager.logChestAction(location, lockChest.getChestId(), ChestLog.ChestAction.PUT_ITEM);
                            }
                        }
                    }
                }
            }
        }
        
        final var title = event.getView().title();
        final var plainTitle = PLAIN_SERIALIZER.serialize(title);
        if (plainTitle.startsWith("Chest Keys")) {
            this.handleKeyGuiClick(event);
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getItem() != null && this.lockChestManager.isKeyItem(event.getItem())) {
            final var player = event.getPlayer();
            final var converted = this.lockChestManager.convertPhysicalToVirtual(player, event.getItem());
            if (converted) {
                final var itemInHand = player.getInventory().getItemInMainHand();
                if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                    if (itemInHand.getAmount() > 1) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    } else {
                        player.getInventory().setItemInMainHand(null);
                    }
                }
                player.updateInventory();
                MessageUtil.sendMessage(player, "<green>Physical key converted to virtual access!");
            }
            event.setCancelled(true);
            return;
        }

        final var clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        if (CONTAINER_MATERIALS.contains(clickedBlock.getType())) {
            final var player = event.getPlayer();
            final var location = clickedBlock.getLocation();
            final var chest = this.lockChestManager.getChestAt(location);
            
            if (chest != null && !chest.isAllowed(player.getUniqueId())) {
                event.setCancelled(true);
                event.setUseInteractedBlock(null);
                MessageUtil.sendMessage(player, "<red>This container is locked!");
                return;
            }
            
            if (chest != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                this.lockChestManager.logChestAction(location, chest.getChestId(), ChestLog.ChestAction.TAKE_ITEM);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        final var item = event.getItemDrop().getItemStack();
        if (this.lockChestManager.isKeyItem(item)) {
            this.lockChestManager.logKeyDrop(event.getPlayer(), item);
        }
    }

    @EventHandler
    public void onEntityPickupItem(final EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof org.bukkit.entity.Player player)) {
            return;
        }

        final var item = event.getItem().getItemStack();
        if (this.lockChestManager.isKeyItem(item)) {
            this.lockChestManager.logKeyPickup(player, item);
        }
    }

    private void handleKeyGuiClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof org.bukkit.entity.Player player)) {
            return;
        }

        final var clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        if (clickedItem.getType() == Material.BARRIER) {
            final var targetBlock = player.getTargetBlockExact(5);
            if (targetBlock != null) {
                final var chest = this.lockChestManager.getChestAt(targetBlock.getLocation());
                if (chest != null && chest.getOwnerUuid().equals(player.getUniqueId())) {
                    this.lockChestManager.unlockChest(chest.getChestId());
                    player.closeInventory();
                    MessageUtil.sendMessage(player, "<green>Chest unlocked!");
                }
            }
            event.setCancelled(true);
        } else if (clickedItem.getType() == Material.NAME_TAG) {
            player.closeInventory();
            MessageUtil.sendMessage(player, "<green>Type the player's name in chat to give them a virtual key:");
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                MessageUtil.sendMessage(player, "<gray>Or type 'cancel' to cancel.");
            }, 1L);
            event.setCancelled(true);
        } else if (clickedItem.getType() == Material.PAPER && this.lockChestManager.isKeyItem(clickedItem)) {
            final var targetBlock = player.getTargetBlockExact(5);
            if (targetBlock != null) {
                final var chest = this.lockChestManager.getChestAt(targetBlock.getLocation());
                if (chest != null && chest.getOwnerUuid().equals(player.getUniqueId())) {
                    final var key = this.lockChestManager.createPhysicalKey(chest);
                    if (key != null) {
                        final var remaining = player.getInventory().addItem(key);
                        if (!remaining.isEmpty()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), key);
                        }
                        MessageUtil.sendMessage(player, "<green>Physical key added to your inventory!");
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        final var title = event.getView().title();
        final var plainTitle = PLAIN_SERIALIZER.serialize(title);
        if (plainTitle.startsWith("Chest Keys")) {
            event.setCancelled(true);
            return;
        }
        
        if (!(event.getWhoClicked() instanceof org.bukkit.entity.Player player)) {
            return;
        }
        
        final var holder = event.getInventory().getHolder();
        if (holder instanceof Chest chest) {
            final var lockChest = this.lockChestManager.getChestAt(chest.getLocation());
            if (lockChest != null && !lockChest.isAllowed(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}
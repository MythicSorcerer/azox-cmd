package com.azox.utils.command.impl.util;

import com.azox.utils.AzoxUtils;
import com.azox.utils.command.BaseCommand;
import com.azox.utils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class SeeCommand extends BaseCommand {

    public static final NamespacedKey INSPECT_TARGET_KEY = new NamespacedKey(AzoxUtils.getInstance(), "inspect_target");

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        String type = "i"; // default to inventory
        String targetName;

        if (label.equalsIgnoreCase("si")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(player, "<red>Usage: /si <player>");
                return;
            }
            targetName = args[0];
        } else if (label.equalsIgnoreCase("se")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(player, "<red>Usage: /se <player>");
                return;
            }
            targetName = args[0];
            type = "e";
        } else {
            if (args.length < 2) {
                MessageUtil.sendMessage(player, "<red>Usage: /see <i|e> <player>");
                return;
            }
            type = args[0].toLowerCase();
            targetName = args[1];
        }

        final Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            MessageUtil.sendMessage(player, "<red>Player not found!");
            return;
        }

        if (type.startsWith("i")) {
            openInventoryInspect(player, target);
        } else if (type.startsWith("e")) {
            player.openInventory(target.getEnderChest());
            MessageUtil.sendMessage(player, "<green>Opening " + target.getName() + "'s enderchest...");
        }
    }

    private void openInventoryInspect(final Player viewer, final Player target) {
        final Inventory inv = Bukkit.createInventory(null, 54, MessageUtil.parse("<gold>Inspecting: " + target.getName()));
        updateInspectInventory(inv, target);
        viewer.openInventory(inv);
        MessageUtil.sendMessage(viewer, "<green>Opening " + target.getName() + "'s inventory...");

        // Live update task
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!viewer.isOnline() || !viewer.getOpenInventory().getTitle().contains("Inspecting: " + target.getName())) {
                    this.cancel();
                    return;
                }
                if (!target.isOnline()) {
                    viewer.closeInventory();
                    this.cancel();
                    return;
                }
                updateInspectInventory(inv, target);
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public static void updateInspectInventory(final Inventory inv, final Player target) {
        final ItemStack[] contents = target.getInventory().getContents(); 
        
        // Row 1: Hotbar (0-8)
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, contents[i]);
        }
        
        // Row 2-4: Main Storage (9-35) -> GUI Slots 9-35
        for (int i = 9; i < 36; i++) {
            inv.setItem(i, contents[i]);
        }

        // Row 5: Armor (36-39), Offhand (40), Cursor (41), Spacers (42-44)
        final ItemStack[] armor = target.getInventory().getArmorContents();
        inv.setItem(36, armor[3] != null && armor[3].getType() != Material.AIR ? armor[3] : createPlaceholder(Material.GRAY_STAINED_GLASS_PANE, "<gray>Helmet Slot"));
        inv.setItem(37, armor[2] != null && armor[2].getType() != Material.AIR ? armor[2] : createPlaceholder(Material.GRAY_STAINED_GLASS_PANE, "<gray>Chestplate Slot"));
        inv.setItem(38, armor[1] != null && armor[1].getType() != Material.AIR ? armor[1] : createPlaceholder(Material.GRAY_STAINED_GLASS_PANE, "<gray>Leggings Slot"));
        inv.setItem(39, armor[0] != null && armor[0].getType() != Material.AIR ? armor[0] : createPlaceholder(Material.GRAY_STAINED_GLASS_PANE, "<gray>Boots Slot"));
        
        inv.setItem(40, target.getInventory().getItemInOffHand() != null && target.getInventory().getItemInOffHand().getType() != Material.AIR ? target.getInventory().getItemInOffHand() : createPlaceholder(Material.GRAY_STAINED_GLASS_PANE, "<gray>Offhand Slot"));
        inv.setItem(41, target.getItemOnCursor() != null && target.getItemOnCursor().getType() != Material.AIR ? target.getItemOnCursor() : createPlaceholder(Material.GRAY_STAINED_GLASS_PANE, "<gray>Cursor Slot"));
        
        final ItemStack spacer = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        final ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.displayName(Component.empty());
        spacer.setItemMeta(spacerMeta);
        inv.setItem(42, createInfoItem(target));
        inv.setItem(43, spacer);
        inv.setItem(44, spacer);

        // Row 6: Open GUI slots (45-53)
        final Inventory topInv = target.getOpenInventory().getTopInventory();
        if (topInv != null && topInv.getType() != org.bukkit.event.inventory.InventoryType.CRAFTING && topInv.getType() != org.bukkit.event.inventory.InventoryType.PLAYER) {
            for (int i = 0; i < 9; i++) {
                if (i < topInv.getSize()) {
                    inv.setItem(45 + i, topInv.getItem(i));
                } else {
                    inv.setItem(45 + i, spacer);
                }
            }
        } else {
            for (int i = 45; i < 54; i++) {
                inv.setItem(i, spacer);
            }
        }
    }

    private static int countItems(Inventory inv) {
        int count = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) count++;
        }
        return count;
    }

    private static ItemStack createInfoItem(Player target) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gold>Player: " + target.getName()));
        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>Health: <red>" + (int)target.getHealth() + "/" + (int)target.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue()));
        lore.add(MessageUtil.parse("<gray>Food: <orange>" + target.getFoodLevel()));
        meta.getPersistentDataContainer().set(INSPECT_TARGET_KEY, PersistentDataType.STRING, target.getUniqueId().toString());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createPlaceholder(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(name));
        item.setItemMeta(meta);
        return item;
    }
}

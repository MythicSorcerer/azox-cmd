package com.azox.utils.manager;

import com.azox.utils.AzoxUtils;
import com.azox.utils.model.Home;
import com.azox.utils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class GuiManager {

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    public static final NamespacedKey HOME_KEY = new NamespacedKey(AzoxUtils.getInstance(), "home_name");
    public static final NamespacedKey UTILITY_KEY = new NamespacedKey(AzoxUtils.getInstance(), "utility_type");
    public static final NamespacedKey ADMIN_KEY = new NamespacedKey(AzoxUtils.getInstance(), "admin_setting");
    public static final NamespacedKey WORLD_KEY = new NamespacedKey(AzoxUtils.getInstance(), "world_name");
    public static final NamespacedKey EC_PAGE_KEY = new NamespacedKey(AzoxUtils.getInstance(), "ec_page");
    public static final NamespacedKey CONFIRM_ACTION_KEY = new NamespacedKey(AzoxUtils.getInstance(), "confirm_action");

    public void openHomesGui(final Player player) {
        final Map<String, Home> homes = plugin.getHomeManager().getHomes(player);
        final Inventory inv = Bukkit.createInventory(null, 54, MessageUtil.parse("<gold>" + MessageUtil.ICON_HOME + " Your Homes"));

        int slot = 0;
        for (final Home home : homes.values()) {
            if (slot >= 53) break;
            
            final ItemStack item = new ItemStack(Material.LIME_CONCRETE);
            final ItemMeta meta = item.getItemMeta();
            meta.displayName(MessageUtil.parse("<green>" + MessageUtil.ICON_HOME + " " + home.getName()));
            meta.getPersistentDataContainer().set(HOME_KEY, PersistentDataType.STRING, home.getName());
            
            final List<Component> lore = new ArrayList<>();
            lore.add(MessageUtil.parse("<gray>" + MessageUtil.ICON_ARROW + " World: " + home.getWorldName()));
            lore.add(MessageUtil.parse("<gray>" + MessageUtil.ICON_ARROW + " X: " + (int) home.getX() + ", Y: " + (int) home.getY() + ", Z: " + (int) home.getZ()));
            if (!home.getDescription().isEmpty()) {
                lore.add(MessageUtil.parse("<gray>" + MessageUtil.ICON_INFO + " Description: " + home.getDescription()));
            }
            lore.add(MessageUtil.parse(""));
            lore.add(MessageUtil.parse("<green>" + MessageUtil.ICON_TP + " Left-Click to Teleport"));
            lore.add(MessageUtil.parse("<red>" + MessageUtil.ICON_UTILITY + " Right-Click to Manage"));
            
            meta.lore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }

        final ItemStack unlock = new ItemStack(Material.GOLD_BLOCK);
        final ItemMeta unlockMeta = unlock.getItemMeta();
        unlockMeta.displayName(MessageUtil.parse("<gold><bold>Unlock More Homes"));
        unlockMeta.lore(List.of(MessageUtil.parse("<gray>Get a higher rank to increase your limit!")));
        unlock.setItemMeta(unlockMeta);
        inv.setItem(53, unlock);

        player.openInventory(inv);
    }

    public void openManageHomeGui(final Player player, final Home home) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>Manage: " + home.getName()));

        final ItemStack info = new ItemStack(Material.BOOK);
        final ItemMeta infoMeta = info.getItemMeta();
        infoMeta.displayName(MessageUtil.parse("<yellow>" + MessageUtil.ICON_INFO + " Home Info"));
        final List<Component> infoLore = new ArrayList<>();
        infoLore.add(MessageUtil.parse("<gray>World: " + home.getWorldName()));
        infoLore.add(MessageUtil.parse("<gray>Coords: " + (int)home.getX() + ", " + (int)home.getY() + ", " + (int)home.getZ()));
        if (!home.getDescription().isEmpty()) infoLore.add(MessageUtil.parse("<gray>Desc: " + home.getDescription()));
        infoMeta.lore(infoLore);
        info.setItemMeta(infoMeta);
        inv.setItem(4, info);

        inv.setItem(10, createHomeActionItem(Material.ENDER_PEARL, "<green>Teleport", "teleport", home.getName()));
        inv.setItem(12, createHomeActionItem(Material.NAME_TAG, "<yellow>Rename", "rename", home.getName()));
        inv.setItem(13, createHomeActionItem(Material.WRITABLE_BOOK, "<yellow>Set Description", "description", home.getName()));
        inv.setItem(14, createHomeActionItem(Material.BEACON, "<aqua>Toggle Public", "public", home.getName()));
        inv.setItem(15, createHomeActionItem(Material.COMPASS, "<gold>Relocate", "relocate", home.getName()));
        inv.setItem(16, createHomeActionItem(Material.BARRIER, "<red>Delete", "delete", home.getName()));

        inv.setItem(22, createBackButton("homes"));

        player.openInventory(inv);
    }

    public void openConfirmGui(final Player player, String action, String targetName) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<red>Confirm: " + action));

        final ItemStack confirm = new ItemStack(Material.LIME_CONCRETE);
        final ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.displayName(MessageUtil.parse("<green><bold>CONFIRM " + action.toUpperCase()));
        confirmMeta.getPersistentDataContainer().set(CONFIRM_ACTION_KEY, PersistentDataType.STRING, action + ":" + targetName);
        confirm.setItemMeta(confirmMeta);

        final ItemStack cancel = new ItemStack(Material.RED_CONCRETE);
        final ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(MessageUtil.parse("<red><bold>CANCEL"));
        cancel.setItemMeta(cancelMeta);

        inv.setItem(11, confirm);
        inv.setItem(15, cancel);

        player.openInventory(inv);
    }

    public void openUtilitiesGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>" + MessageUtil.ICON_UTILITY + " Server Utilities"));

        if (player.hasPermission("azox.util.default.craft")) inv.setItem(10, createGuiItem(Material.CRAFTING_TABLE, "<green>Crafting Table", "craft"));
        if (player.hasPermission("azox.util.player.grindstone")) inv.setItem(11, createGuiItem(Material.GRINDSTONE, "<green>Grindstone", "grindstone"));
        if (player.hasPermission("azox.util.player.stonecutter")) inv.setItem(12, createGuiItem(Material.STONECUTTER, "<green>Stonecutter", "stonecutter"));
        if (player.hasPermission("azox.util.default.enderchest")) inv.setItem(13, createGuiItem(Material.ENDER_CHEST, "<green>Ender Chest", "ec"));
        if (player.hasPermission("azox.util.player.anvil")) inv.setItem(14, createGuiItem(Material.ANVIL, "<green>Anvil", "anvil"));
        if (player.hasPermission("azox.util.player.cartographytable")) inv.setItem(15, createGuiItem(Material.CARTOGRAPHY_TABLE, "<green>Cartography Table", "carttable"));

        player.openInventory(inv);
    }

    public void openAdminGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<red>" + MessageUtil.ICON_STAR + " Admin Configuration"));

        inv.setItem(4, createAdminItem(Material.ENDER_EYE, "<aqua>Vanish Settings", "vanish_settings", true));
        inv.setItem(13, new ItemStack(Material.COMPARATOR));

        boolean guiEnabled = plugin.getPlayerStorage().isGuiEnabled(player);
        inv.setItem(2, createAdminItem(Material.BOOK, "<yellow>GUI Mode", "toggle_gui", guiEnabled));
        inv.setItem(11, new ItemStack(guiEnabled ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE));

        inv.setItem(6, createAdminItem(Material.COMPASS, "<green>World Selector", "world_selector", true));
        inv.setItem(15, new ItemStack(Material.DAYLIGHT_DETECTOR));
        
        boolean mobsIgnore = plugin.getPlayerStorage().isGodMobsIgnore(player);
        inv.setItem(8, createAdminItem(Material.ZOMBIE_HEAD, "<red>Mob Targeting", "toggle_mobs", !mobsIgnore));
        inv.setItem(17, new ItemStack(!mobsIgnore ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE));

        player.openInventory(inv);
    }

    public void openVanishGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<aqua>" + MessageUtil.ICON_INFO + " Vanish Settings"));

        boolean fakeMsg = plugin.getPlayerStorage().isVanishFakeMessages(player);
        boolean autoFly = plugin.getPlayerStorage().isVanishAutoFly(player);
        boolean autoGod = plugin.getPlayerStorage().isVanishAutoGod(player);
        boolean pickup = !plugin.getPlayerStorage().isVanishPickupDisabled(player);

        inv.setItem(10, createAdminItem(Material.PAPER, "<yellow>Fake Join/Leave", "v_fake_msg", fakeMsg));
        inv.setItem(19, new ItemStack(fakeMsg ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE));

        inv.setItem(12, createAdminItem(Material.FEATHER, "<yellow>Auto Fly", "v_auto_fly", autoFly));
        inv.setItem(21, new ItemStack(autoFly ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE));

        inv.setItem(14, createAdminItem(Material.GOLDEN_APPLE, "<yellow>Auto God", "v_auto_god", autoGod));
        inv.setItem(23, new ItemStack(autoGod ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE));

        inv.setItem(16, createAdminItem(Material.HOPPER, "<yellow>Item Pickup", "v_pickup", pickup));
        inv.setItem(25, new ItemStack(pickup ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE));

        inv.setItem(22, createBackButton("admin"));

        player.openInventory(inv);
    }

    public void openWorldSelectorGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<green>" + MessageUtil.ICON_WARP + " World Selector"));

        inv.setItem(11, createWorldItem(Material.GRASS_BLOCK, "<green>Survival", "world"));
        inv.setItem(13, createWorldItem(Material.BEACON, "<gold>Lobby", "lobby"));
        
        inv.setItem(22, createBackButton("admin"));

        player.openInventory(inv);
    }

    public void openEnderChestPageSelector(final Player player, int maxPages) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>Ender Chest Pages"));
        for (int i = 1; i <= maxPages; i++) {
            inv.setItem(10 + i, createEcPageItem(i));
        }
        player.openInventory(inv);
    }

    public void openEnderChestPage(final Player player, int page) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>Ender Chest - Page " + page));
        inv.setContents(plugin.getPlayerStorage().getEnderChestPage(player, page));
        player.openInventory(inv);
    }

    private ItemStack createGuiItem(final Material material, final String name, final String type) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(name));
        meta.getPersistentDataContainer().set(UTILITY_KEY, PersistentDataType.STRING, type);
        final List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>Click to open!"));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createAdminItem(final Material material, final String name, final String key, final boolean enabled) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(name));
        meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, key);
        final List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>Status: " + (enabled ? "<green>Enabled" : "<red>Disabled")));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createHomeActionItem(Material material, String name, String action, String homeName) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(name));
        meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "home_action:" + action + ":" + homeName);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBackButton(String target) {
        final ItemStack item = new ItemStack(Material.BARRIER);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<red>Back"));
        meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "back_to:" + target);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createWorldItem(final Material material, final String name, final String worldName) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(name));
        meta.getPersistentDataContainer().set(WORLD_KEY, PersistentDataType.STRING, worldName);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createEcPageItem(int page) {
        final ItemStack item = new ItemStack(Material.ENDER_CHEST);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<green>Page " + page));
        meta.getPersistentDataContainer().set(EC_PAGE_KEY, PersistentDataType.INTEGER, page);
        item.setItemMeta(meta);
        return item;
    }
}

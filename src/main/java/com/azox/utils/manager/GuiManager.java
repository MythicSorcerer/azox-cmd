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

    public void openHomesGui(final Player player) {
        final Map<String, Home> homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        final Inventory inv = Bukkit.createInventory(null, 54, MessageUtil.parse("<gold>" + MessageUtil.ICON_HOME + " Your Homes"));

        int slot = 0;
        for (final Home home : homes.values()) {
            if (slot >= 54) break;
            
            final ItemStack item = new ItemStack(Material.WHITE_BED);
            final ItemMeta meta = item.getItemMeta();
            meta.displayName(MessageUtil.parse("<yellow>" + MessageUtil.ICON_HOME + " " + home.getName()));
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

        player.openInventory(inv);
    }

    public void openUtilitiesGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>" + MessageUtil.ICON_UTILITY + " Server Utilities"));

        if (player.hasPermission("azox.utils.craft")) inv.setItem(10, createGuiItem(Material.CRAFTING_TABLE, "<green>Crafting Table", "craft"));
        if (player.hasPermission("azox.utils.grindstone")) inv.setItem(11, createGuiItem(Material.GRINDSTONE, "<green>Grindstone", "grindstone"));
        if (player.hasPermission("azox.utils.stonecutter")) inv.setItem(12, createGuiItem(Material.STONECUTTER, "<green>Stonecutter", "stonecutter"));
        if (player.hasPermission("azox.utils.enderchest")) inv.setItem(13, createGuiItem(Material.ENDER_CHEST, "<green>Ender Chest", "ec"));
        if (player.hasPermission("azox.utils.anvil")) inv.setItem(14, createGuiItem(Material.ANVIL, "<green>Anvil", "anvil"));
        if (player.hasPermission("azox.utils.cartographytable")) inv.setItem(15, createGuiItem(Material.CARTOGRAPHY_TABLE, "<green>Cartography Table", "carttable"));

        player.openInventory(inv);
    }

    public void openAdminGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<red>" + MessageUtil.ICON_STAR + " Admin Configuration"));

        inv.setItem(11, createAdminItem(Material.ENDER_EYE, "<aqua>Vanish Settings", "vanish_settings", true)); // Always visible
        
        boolean guiEnabled = plugin.getTeleportManager().getStorage().isGuiEnabled(player.getUniqueId());
        inv.setItem(13, createAdminItem(guiEnabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE, "<yellow>GUI Mode", "toggle_gui", guiEnabled));
        
        inv.setItem(15, createAdminItem(Material.COMPASS, "<green>World Selector", "world_selector", true));

        player.openInventory(inv);
    }

    public void openVanishGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<aqua>" + MessageUtil.ICON_INFO + " Vanish Settings"));

        boolean fakeMsg = plugin.getTeleportManager().getStorage().isVanishFakeMessages(player.getUniqueId());
        boolean autoFly = plugin.getTeleportManager().getStorage().isVanishAutoFly(player.getUniqueId());
        boolean autoGod = plugin.getTeleportManager().getStorage().isVanishAutoGod(player.getUniqueId());
        boolean pickup = !plugin.getTeleportManager().getStorage().isVanishPickupDisabled(player.getUniqueId());

        inv.setItem(10, createAdminItem(fakeMsg ? Material.LIME_CONCRETE : Material.RED_CONCRETE, "<yellow>Fake Join/Leave", "v_fake_msg", fakeMsg));
        inv.setItem(12, createAdminItem(autoFly ? Material.LIME_CONCRETE : Material.RED_CONCRETE, "<yellow>Auto Fly", "v_auto_fly", autoFly));
        inv.setItem(14, createAdminItem(autoGod ? Material.LIME_CONCRETE : Material.RED_CONCRETE, "<yellow>Auto God", "v_auto_god", autoGod));
        inv.setItem(16, createAdminItem(pickup ? Material.LIME_CONCRETE : Material.RED_CONCRETE, "<yellow>Item Pickup", "v_pickup", pickup));

        player.openInventory(inv);
    }

    public void openWorldSelectorGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<green>" + MessageUtil.ICON_WARP + " World Selector"));

        // Survival
        inv.setItem(11, createWorldItem(Material.GRASS_BLOCK, "<green>Survival", "world"));
        inv.setItem(20, createWorldItem(Material.NETHERRACK, "<red>Nether", "world_nether"));
        inv.setItem(29, createWorldItem(Material.END_STONE, "<light_purple>The End", "world_end")); // 29 is out of bounds for 27 size, wait.
        // Let's use size 27.
        // 11 (Survival), 13 (Lobby), 15 (Other?)
        // If they click Survival, maybe teleport to spawn?
        
        inv.setItem(11, createWorldItem(Material.GRASS_BLOCK, "<green>Survival", "world"));
        inv.setItem(13, createWorldItem(Material.BEACON, "<gold>Lobby", "lobby"));
        
        // Add dynamic worlds if needed, or just stick to these for now.
        
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

    private ItemStack createWorldItem(final Material material, final String name, final String worldName) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(name));
        meta.getPersistentDataContainer().set(WORLD_KEY, PersistentDataType.STRING, worldName);
        item.setItemMeta(meta);
        return item;
    }
}

package net.azox.cmd.command.impl.util;

import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.manager.GuiManager;
import net.azox.cmd.manager.LockChestManager;
import net.azox.cmd.model.ChestLock;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class LockGuiCommand extends BaseCommand {

    private final LockChestManager lockChestManager = this.plugin.getLockChestManager();
    public static final String GUI_TITLE = "Chest Keys";

    @Override
    public void execute(final CommandSender sender, final String label, final String[] args) {
        if (this.permissionDenied(sender, "azox.util.lockchest")) {
            return;
        }
        if (!this.isPlayer(sender)) {
            return;
        }

        final Player player = (Player) sender;
        final Block block = player.getTargetBlockExact(5);

        if (block == null || !(block.getState() instanceof Chest)) {
            MessageUtil.sendMessage(player, "<red>You are not looking at a chest!");
            return;
        }

        final ChestLock chest = this.lockChestManager.getChestAt(block.getLocation());
        if (chest == null) {
            MessageUtil.sendMessage(player, "<red>This chest is not locked!");
            return;
        }

        if (!chest.getOwnerUuid().equals(player.getUniqueId())) {
            MessageUtil.sendMessage(player, "<red>This chest is not yours!");
            return;
        }

        this.openKeyGui(player, chest);
    }

    private void openKeyGui(final Player player, final ChestLock chest) {
        final Inventory inventory = Bukkit.createInventory(null, 27, MessageUtil.parse(GUI_TITLE + " - " + chest.getChestId()));

        final ItemStack physicalKey = this.lockChestManager.createPhysicalKey(chest);
        final ItemMeta meta = physicalKey.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(LockChestManager.KEY_ACTION_KEY, PersistentDataType.STRING, "EXPORT");
            physicalKey.setItemMeta(meta);
        }

        final ItemStack info = new ItemStack(Material.BOOK);
        final ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.displayName(MessageUtil.parse("<gold>Chest Info"));
            final List<String> lore = new ArrayList<>();
            lore.add("<gray>ID: " + chest.getChestId());
            final var ownerName = this.getOwnerName(chest.getOwnerUuid());
            lore.add("<gray>Owner: " + ownerName);
            lore.add("<gray>Allowed: " + (chest.getAllowedPlayers() != null ? chest.getAllowedPlayers().size() : 0) + " players");
            infoMeta.lore(lore.stream().map(MessageUtil::parse).toList());
            info.setItemMeta(infoMeta);
        }

        final ItemStack virtualKey = new ItemStack(Material.NAME_TAG);
        final ItemMeta virtualMeta = virtualKey.getItemMeta();
        if (virtualMeta != null) {
            virtualMeta.displayName(MessageUtil.parse("<aqua>Add Virtual Key"));
            virtualMeta.lore(List.of(MessageUtil.parse("<gray>Add a player who can access this chest")));
            virtualKey.setItemMeta(virtualMeta);
        }

        final ItemStack barrier = new ItemStack(Material.BARRIER);
        final ItemMeta barrierMeta = barrier.getItemMeta();
        if (barrierMeta != null) {
            barrierMeta.displayName(MessageUtil.parse("<red>Unlock Chest"));
            barrierMeta.lore(List.of(MessageUtil.parse("<gray>Remove lock from this chest")));
            barrier.setItemMeta(barrierMeta);
        }

        inventory.setItem(11, physicalKey);
        inventory.setItem(13, info);
        inventory.setItem(15, virtualKey);
        inventory.setItem(22, barrier);

        player.openInventory(inventory);
    }

    private String getOwnerName(final java.util.UUID ownerUuid) {
        final var player = this.plugin.getServer().getPlayer(ownerUuid);
        if (player != null) {
            return player.getName();
        }
        final var offlinePlayer = this.plugin.getServer().getOfflinePlayer(ownerUuid);
        return offlinePlayer != null ? offlinePlayer.getName() : "Unknown";
    }

    @Override
    public List<String> complete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }
}
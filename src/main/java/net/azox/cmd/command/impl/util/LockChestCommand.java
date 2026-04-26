package net.azox.cmd.command.impl.util;

import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.manager.LockChestManager;
import net.azox.cmd.model.ChestLock;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class LockChestCommand extends BaseCommand {

    private final LockChestManager lockChestManager = this.plugin.getLockChestManager();

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

        final ChestLock existingChest = this.lockChestManager.getChestAt(block.getLocation());
        if (existingChest != null) {
            MessageUtil.sendMessage(player, "<red>This chest is already locked!");
            return;
        }

        final ChestLock lockedChest = this.lockChestManager.lockChest(player, block);
        if (lockedChest == null) {
            MessageUtil.sendMessage(player, "<red>Failed to lock the chest!");
            return;
        }

        MessageUtil.sendMessage(player, "<green>Chest locked successfully! ID: " + lockedChest.getChestId());
    }

    @Override
    public List<String> complete(final CommandSender sender, final String[] args) {
        return new java.util.ArrayList<>();
    }
}
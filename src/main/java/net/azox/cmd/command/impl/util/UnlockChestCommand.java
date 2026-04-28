package net.azox.cmd.command.impl.util;

import net.azox.cmd.command.BaseCommand;
import net.azox.cmd.manager.LockChestManager;
import net.azox.cmd.model.ChestLock;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class UnlockChestCommand extends BaseCommand {

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

        final ChestLock chest = this.lockChestManager.getChestAt(block.getLocation());
        if (chest == null) {
            MessageUtil.sendMessage(player, "<red>This chest is not locked!");
            return;
        }

        if (!chest.getOwnerUuid().equals(player.getUniqueId())) {
            MessageUtil.sendMessage(player, "<red>You cannot unlock someone else's chest!");
            return;
        }

        this.lockChestManager.unlockChest(chest.getChestId());
        MessageUtil.sendMessage(player, "<green>Chest unlocked!");
    }

    @Override
    public List<String> complete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }
}
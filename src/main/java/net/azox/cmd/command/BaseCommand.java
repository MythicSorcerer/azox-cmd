package net.azox.cmd.command;

import net.azox.cmd.AzoxCmd;
import net.azox.cmd.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    protected final AzoxCmd plugin = AzoxCmd.getInstance();

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        this.execute(sender, label, args);
        return true;
    }

    public abstract void execute(CommandSender sender, String label, String[] args);

    protected boolean isPlayer(final CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        }
        MessageUtil.sendMessage(sender, "<red>Only players can use this command!");
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
        return this.complete(sender, args);
    }

    public List<String> complete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    protected List<String> getVisiblePlayerNames(final CommandSender sender, final String partial) {
        if (partial == null) {
            return new ArrayList<>();
        }
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> !(sender instanceof Player) || this.plugin.getVanishManager().canSee((Player) sender, player))
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(partial.toLowerCase()))
                .collect(Collectors.toList());
    }

    protected boolean hasPermission(final CommandSender sender, final String permission) {
        return sender.hasPermission(permission);
    }

    protected boolean permissionDenied(final CommandSender sender, final String permission) {
        if (!this.hasPermission(sender, permission)) {
            MessageUtil.sendMessage(sender, "<red>You don't have permission to use this command!");
            return true;
        }
        return false;
    }
}

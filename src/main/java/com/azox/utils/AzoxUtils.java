package com.azox.utils;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class AzoxUtils extends JavaPlugin {

    @Getter
    private static AzoxUtils instance;

    private final com.azox.utils.manager.PlayerDataManager playerDataManager = new com.azox.utils.manager.PlayerDataManager();
    private final com.azox.utils.storage.PlayerStorage playerStorage = new com.azox.utils.storage.PlayerStorage();
    private final com.azox.utils.manager.HomeManager homeManager = new com.azox.utils.manager.HomeManager();
    private final com.azox.utils.manager.WarpManager warpManager = new com.azox.utils.manager.WarpManager();
    private final com.azox.utils.manager.TeleportManager teleportManager = new com.azox.utils.manager.TeleportManager();
    private final com.azox.utils.manager.FreezeManager freezeManager = new com.azox.utils.manager.FreezeManager();
    private final com.azox.utils.manager.GuiManager guiManager = new com.azox.utils.manager.GuiManager();
    private final com.azox.utils.manager.JailManager jailManager = new com.azox.utils.manager.JailManager();
    private final com.azox.utils.manager.VanishManager vanishManager = new com.azox.utils.manager.VanishManager();
    private final com.azox.utils.manager.KitManager kitManager = new com.azox.utils.manager.KitManager();
    private final com.azox.utils.manager.ParticleManager particleManager = new com.azox.utils.manager.ParticleManager();

    @Override
    public void onEnable() {
        AzoxUtils.instance = this;

        this.registerCommands();
        this.registerListeners();

        this.getLogger().info("AzoxUtils has been enabled!");
    }

    private void registerCommands() {
        final java.util.Map<String, org.bukkit.command.CommandExecutor> commandMap = new java.util.HashMap<>();

        // Home commands
        commandMap.put("sethome", new com.azox.utils.command.impl.home.SetHomeCommand());
        commandMap.put("home", new com.azox.utils.command.impl.home.HomeCommand());
        commandMap.put("delhome", new com.azox.utils.command.impl.home.DelHomeCommand());
        commandMap.put("homes", new com.azox.utils.command.impl.home.HomesCommand());
        commandMap.put("edithome", new com.azox.utils.command.impl.home.EditHomeCommand());
        commandMap.put("phome", new com.azox.utils.command.impl.home.PHomeCommand());

        // Warp commands
        commandMap.put("setwarp", new com.azox.utils.command.impl.warp.SetWarpCommand());
        commandMap.put("warp", new com.azox.utils.command.impl.warp.WarpCommand());

        // Teleport commands
        commandMap.put("tpa", new com.azox.utils.command.impl.teleport.TpaCommand());
        commandMap.put("tpahere", new com.azox.utils.command.impl.teleport.TpaHereCommand());
        commandMap.put("tpaccept", new com.azox.utils.command.impl.teleport.TpAcceptCommand());
        commandMap.put("tpdecline", new com.azox.utils.command.impl.teleport.TpDeclineCommand());
        commandMap.put("tpignore", new com.azox.utils.command.impl.teleport.TpIgnoreCommand());
        commandMap.put("back", new com.azox.utils.command.impl.teleport.BackCommand());
        commandMap.put("rtp", new com.azox.utils.command.impl.teleport.RtpCommand());
        commandMap.put("tpo", new com.azox.utils.command.impl.teleport.TpoCommand());
        commandMap.put("tpohere", new com.azox.utils.command.impl.teleport.TpoCommand());
        commandMap.put("tpoundo", new com.azox.utils.command.impl.teleport.TpoCommand());

        // Inventory util commands
        commandMap.put("enderchest", new com.azox.utils.command.impl.util.InventoryUtilCommands("enderchest"));
        commandMap.put("anvil", new com.azox.utils.command.impl.util.InventoryUtilCommands("anvil"));
        commandMap.put("cartographytable", new com.azox.utils.command.impl.util.InventoryUtilCommands("cartographytable"));
        commandMap.put("loom", new com.azox.utils.command.impl.util.InventoryUtilCommands("loom"));
        commandMap.put("trash", new com.azox.utils.command.impl.util.InventoryUtilCommands("trash"));
        commandMap.put("craft", new com.azox.utils.command.impl.util.InventoryUtilCommands("craft"));
        commandMap.put("grindstone", new com.azox.utils.command.impl.util.InventoryUtilCommands("grindstone"));
        commandMap.put("stonecutter", new com.azox.utils.command.impl.util.InventoryUtilCommands("stonecutter"));
        commandMap.put("utilities", new com.azox.utils.command.impl.util.GuiCommand());
        commandMap.put("config", new com.azox.utils.command.impl.util.ConfigCommand());
        commandMap.put("settings", new com.azox.utils.command.impl.util.SettingsCommand());

        // See commands
        commandMap.put("see", new com.azox.utils.command.impl.util.SeeCommand());
        commandMap.put("si", new com.azox.utils.command.impl.util.SeeCommand());
        commandMap.put("se", new com.azox.utils.command.impl.util.SeeCommand());

        // Jail commands
        commandMap.put("jail", new com.azox.utils.command.impl.util.JailCommand());
        commandMap.put("setjail", new com.azox.utils.command.impl.util.JailCommand());
        commandMap.put("deljail", new com.azox.utils.command.impl.util.JailCommand());
        commandMap.put("unjail", new com.azox.utils.command.impl.util.JailCommand());

        // Admin commands
        commandMap.put("azox", new com.azox.utils.command.impl.util.AdminCommand());
        commandMap.put("lobby", new com.azox.utils.command.impl.util.LobbyCommand());
        commandMap.put("remove", new com.azox.utils.command.impl.util.RemoveCommand());
        commandMap.put("createkit", new com.azox.utils.command.impl.util.KitCommands());
        commandMap.put("kit", new com.azox.utils.command.impl.util.KitCommands());
        commandMap.put("delkit", new com.azox.utils.command.impl.util.KitCommands());

        // Player util commands
        commandMap.put("feed", new com.azox.utils.command.impl.util.PlayerUtilCommands("feed"));
        commandMap.put("heal", new com.azox.utils.command.impl.util.PlayerUtilCommands("heal"));
        commandMap.put("fly", new com.azox.utils.command.impl.util.PlayerUtilCommands("fly"));
        commandMap.put("god", new com.azox.utils.command.impl.util.PlayerUtilCommands("god"));

        // Navigation commands
        commandMap.put("top", new com.azox.utils.command.impl.util.NavigationCommands("top"));
        commandMap.put("jumpto", new com.azox.utils.command.impl.util.NavigationCommands("jumpto"));
        commandMap.put("near", new com.azox.utils.command.impl.util.NavigationCommands("near"));
        commandMap.put("world", new com.azox.utils.command.impl.util.NavigationCommands("world"));

        // Gamemode commands
        commandMap.put("gamemode", new com.azox.utils.command.impl.util.GamemodeCommand(null));
        commandMap.put("gms", new com.azox.utils.command.impl.util.GamemodeCommand(org.bukkit.GameMode.SURVIVAL));
        commandMap.put("gmc", new com.azox.utils.command.impl.util.GamemodeCommand(org.bukkit.GameMode.CREATIVE));
        commandMap.put("gma", new com.azox.utils.command.impl.util.GamemodeCommand(org.bukkit.GameMode.ADVENTURE));
        commandMap.put("gmsp", new com.azox.utils.command.impl.util.GamemodeCommand(org.bukkit.GameMode.SPECTATOR));

        // Other util commands
        commandMap.put("speed", new com.azox.utils.command.impl.util.SpeedCommand());
        commandMap.put("weather", new com.azox.utils.command.impl.util.WeatherCommand("weather"));
        commandMap.put("sun", new com.azox.utils.command.impl.util.WeatherCommand("sun"));
        commandMap.put("storm", new com.azox.utils.command.impl.util.WeatherCommand("storm"));
        commandMap.put("setspawn", new com.azox.utils.command.impl.util.SpawnCommand(true));

        // Item modification commands
        commandMap.put("itemname", new com.azox.utils.command.impl.util.ItemModCommands("itemname"));
        commandMap.put("copyitem", new com.azox.utils.command.impl.util.ItemModCommands("copyitem"));
        commandMap.put("repair", new com.azox.utils.command.impl.util.ItemModCommands("repair"));
        commandMap.put("enchant", new com.azox.utils.command.impl.util.ItemModCommands("enchant"));
        commandMap.put("lore", new com.azox.utils.command.impl.util.ItemModCommands("lore"));
        commandMap.put("rules", new com.azox.utils.command.impl.util.RulesCommand());

        // System commands
        commandMap.put("tps", new com.azox.utils.command.impl.util.SystemCommands("tps"));
        commandMap.put("ping", new com.azox.utils.command.impl.util.SystemCommands("ping"));
        commandMap.put("uptime", new com.azox.utils.command.impl.util.SystemCommands("uptime"));
        commandMap.put("stats", new com.azox.utils.command.impl.util.SystemCommands("stats"));
        commandMap.put("azoxreload", new com.azox.utils.command.impl.util.SystemCommands("azoxreload"));

        // Admin commands
        commandMap.put("sudo", new com.azox.utils.command.impl.util.AdminUtilCommands("sudo"));
        commandMap.put("lightning", new com.azox.utils.command.impl.util.AdminUtilCommands("lightning"));
        commandMap.put("burn", new com.azox.utils.command.impl.util.AdminUtilCommands("burn"));
        commandMap.put("extinguish", new com.azox.utils.command.impl.util.AdminUtilCommands("extinguish"));
        commandMap.put("freeze", new com.azox.utils.command.impl.util.AdminUtilCommands("freeze"));
        commandMap.put("vanish", new com.azox.utils.command.impl.util.VanishCommand());
        commandMap.put("nightvision", new com.azox.utils.command.impl.util.NightVisionCommand());
        commandMap.put("nv", new com.azox.utils.command.impl.util.NightVisionCommand());
        commandMap.put("nvt", new com.azox.utils.command.impl.util.NightVisionCommand());
        commandMap.put("nightvisiontoggle", new com.azox.utils.command.impl.util.NightVisionCommand());

        // Misc commands
        commandMap.put("tp", new com.azox.utils.command.impl.util.TpCommand());
        commandMap.put("condense", new com.azox.utils.command.impl.util.CondenseCommand());
        commandMap.put("clearinventory", new com.azox.utils.command.impl.util.ClearInventoryCommand());
        commandMap.put("getpos", new com.azox.utils.command.impl.util.MiscUtilCommands("getpos"));
        commandMap.put("whois", new com.azox.utils.command.impl.util.MiscUtilCommands("whois"));
        commandMap.put("broadcast", new com.azox.utils.command.impl.util.MiscUtilCommands("broadcast"));
        commandMap.put("suicide", new com.azox.utils.command.impl.util.MiscUtilCommands("suicide"));
        commandMap.put("break", new com.azox.utils.command.impl.util.MiscUtilCommands("break"));
        commandMap.put("compass", new com.azox.utils.command.impl.util.MiscUtilCommands("compass"));

        // Register all commands
        for (final java.util.Map.Entry<String, org.bukkit.command.CommandExecutor> entry : commandMap.entrySet()) {
            final org.bukkit.command.PluginCommand command = this.getCommand(entry.getKey());
            if (command != null) {
                command.setExecutor(entry.getValue());
            }
        }
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new com.azox.utils.listener.TeleportListener(), this);
        this.getServer().getPluginManager().registerEvents(new com.azox.utils.listener.PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new com.azox.utils.listener.InventoryListener(), this);
    }

    @Override
    public void onDisable() {
        this.getLogger().info("AzoxUtils has been disabled!");
    }
}

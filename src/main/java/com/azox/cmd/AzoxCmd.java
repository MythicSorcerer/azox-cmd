package com.azox.cmd;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

/**
 * Main class for the AzoxCmd plugin.
 */
@Getter
public class AzoxCmd extends JavaPlugin {
    private static AzoxCmd instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        // Register command executors
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("flyspeed").setExecutor(new FlySpeedCommand());
        getCommand("top").setExecutor(new TopCommand());
        getCommand("anvil").setExecutor(new AnvilCommand());
        getCommand("craft").setExecutor(new CraftCommand());
        getCommand("wb").setExecutor(new WbCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
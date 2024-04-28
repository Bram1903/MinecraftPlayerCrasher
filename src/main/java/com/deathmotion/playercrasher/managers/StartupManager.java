package com.deathmotion.playercrasher.managers;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.commands.CrashCommand;
import com.deathmotion.playercrasher.commands.PCCommand;
import com.deathmotion.playercrasher.events.CrashDetector;

/**
 * Manages the start-up processes of the plugin, including the registration of commands and events.
 */
public class StartupManager {

    private final PlayerCrasher plugin;

    /**
     * Creates a new StartUpManager instance.
     *
     * @param plugin the instance of the plugin class.
     */
    public StartupManager(PlayerCrasher plugin) {
        this.plugin = plugin;

        load();
    }

    /**
     * Calls methods to register commands and events.
     */
    private void load() {
        registerCommands();
        registerEvents();
    }

    /**
     * Registers commands related to the plugin.
     */
    private void registerCommands() {
        plugin.getCommand("playercrasher").setExecutor(new PCCommand(plugin));
        plugin.getCommand("crash").setExecutor(new CrashCommand(plugin));
    }

    /**
     * Registers events related to the plugin.
     */
    private void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(new CrashDetector(this.plugin), this.plugin);
    }
}
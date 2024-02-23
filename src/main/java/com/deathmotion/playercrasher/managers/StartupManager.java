package com.deathmotion.playercrasher.managers;

import co.aikar.commands.PaperCommandManager;
import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.commands.PCCommand;
import com.deathmotion.playercrasher.events.CrashDetector;

public class StartupManager {

    private final PlayerCrasher plugin;
    private final PaperCommandManager manager;

    public StartupManager(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.manager = plugin.getCommandManager();

        load();
    }

    private void load() {
        registerCommands();
        registerEvents();
    }

    private void registerCommands() {
        manager.registerCommand(new PCCommand(this.plugin));
    }

    private void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(new CrashDetector(this.plugin), this.plugin);
    }
}
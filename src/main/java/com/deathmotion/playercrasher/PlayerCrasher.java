package com.deathmotion.playercrasher;

import com.deathmotion.playercrasher.listeners.BrandHandler;
import com.deathmotion.playercrasher.listeners.TransactionHandler;
import com.deathmotion.playercrasher.managers.ConfigManager;
import com.deathmotion.playercrasher.managers.CrashManager;
import com.deathmotion.playercrasher.managers.StartupManager;
import com.deathmotion.playercrasher.managers.UpdateManager;
import com.deathmotion.playercrasher.util.AdventureCompatUtil;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.bstats.Metrics;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the PlayerCrasher plugin.
 * Handles the setup, starting, and stopping of the plugin.
 */
@Getter
public class PlayerCrasher extends JavaPlugin {
    private AdventureCompatUtil adventureCompatUtil;
    private ConfigManager configManager;
    private CrashManager crashManager;

    @Override
    public void onEnable() {
        adventureCompatUtil = new AdventureCompatUtil();

        configManager = new ConfigManager(this);
        crashManager = new CrashManager(this);

        PacketEvents.getAPI().getEventManager().registerListener(new TransactionHandler(this));
        PacketEvents.getAPI().getEventManager().registerListener(new BrandHandler(this));
        PacketEvents.getAPI().init();

        new UpdateManager(this);
        new StartupManager(this);

        enableBStats();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        getLogger().info("Plugin has been uninitialized!");
    }

    /**
     * Enable the bStats plugin statistics system.
     * This method catches and logs any exceptions that might be thrown during the enabling process.
     */
    private void enableBStats() {
        try {
            new Metrics(this, 16190);
        } catch (Exception e) {
            getLogger().warning("Something went wrong while enabling bStats.\n" + e.getMessage());
        }
    }
}

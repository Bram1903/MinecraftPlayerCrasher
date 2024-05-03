package com.deathmotion.playercrasher;

import com.deathmotion.playercrasher.managers.ConfigManager;
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
    private ConfigManager configManager;
    private AdventureCompatUtil adventureCompatUtil;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        adventureCompatUtil = new AdventureCompatUtil();

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

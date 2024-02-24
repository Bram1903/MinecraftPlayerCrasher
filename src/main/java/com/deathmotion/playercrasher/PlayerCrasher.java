package com.deathmotion.playercrasher;

import co.aikar.commands.PaperCommandManager;
import com.deathmotion.playercrasher.managers.ConfigManager;
import com.deathmotion.playercrasher.managers.CrashManager;
import com.deathmotion.playercrasher.managers.StartupManager;
import com.deathmotion.playercrasher.managers.UpdateManager;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the PlayerCrasher plugin.
 * Handles the setup, starting, and stopping of the plugin.
 */
@Getter
public class PlayerCrasher extends JavaPlugin {
    private ConfigManager configManager;
    private BukkitAudiences adventure;
    private PaperCommandManager commandManager;
    private CrashManager crashManager;

    /**
     * Called when the plugin is loaded.
     * This method initializes the PacketEvents API.
     */
    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(false)
                .bStats(true);

        PacketEvents.getAPI().load();
    }

    /**
     * Called when the plugin is enabled.
     * This method initializes the BukkitAudiences instance, the PaperCommandManager, the CrashManager,
     * and starts the UpdateManager and StartupManager.
     * Finally, it enables bStats.
     */
    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        adventure = BukkitAudiences.create(this);
        commandManager = new PaperCommandManager(this);
        crashManager = new CrashManager();

        new UpdateManager(this);
        new StartupManager(this);

        enableBStats();
    }

    /**
     * Called when the plugin is disabled.
     * This method terminates the PacketEvents API and closes the BukkitAudiences instance.
     */
    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        adventure.close();
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

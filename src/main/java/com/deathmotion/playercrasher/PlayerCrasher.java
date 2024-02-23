package com.deathmotion.playercrasher;

import co.aikar.commands.PaperCommandManager;
import com.deathmotion.playercrasher.managers.StartupManager;
import com.deathmotion.playercrasher.managers.UpdateManager;
import com.deathmotion.playercrasher.services.CrashService;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PlayerCrasher extends JavaPlugin {
    private BukkitAudiences adventure;
    private PaperCommandManager commandManager;
    private CrashService crashService;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(false)
                .bStats(true);

        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);
        commandManager = new PaperCommandManager(this);
        crashService = new CrashService(this);

        new UpdateManager(this);
        new StartupManager(this);

        enableBStats();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        adventure.close();
        getLogger().info("Plugin has been uninitialized!");
    }

    private void enableBStats() {
        try {
            new Metrics(this, 16190);
        } catch (Exception e) {
            getLogger().warning("Something went wrong while enabling bStats.\n" + e.getMessage());
        }
    }
}

package com.deathmotion.playercrasher.events;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.managers.CrashManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    private final CrashManager crashManager;

    public PlayerQuit(PlayerCrasher plugin) {
        this.crashManager = plugin.getCrashManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        crashManager.removeClientBrand(event.getPlayer().getUniqueId());
    }
}

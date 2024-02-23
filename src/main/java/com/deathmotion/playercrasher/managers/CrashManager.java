package com.deathmotion.playercrasher.managers;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.models.CrashData;
import com.deathmotion.playercrasher.services.CrashService;
import io.github.retrooper.packetevents.util.FoliaCompatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CrashManager {
    private final ConcurrentHashMap<UUID, CrashData> crashedPlayers = new ConcurrentHashMap<>();

    private final PlayerCrasher plugin;
    private final CrashService crashService;

    public CrashManager(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.crashService = new CrashService();
    }

    public void crashPlayer(CommandSender sender, Player target, CrashMethod method) {
        CrashData crashData = new CrashData();
        crashData.setCrasher(sender);
        crashData.setTarget(target);
        crashData.setMethod(method);

        crashedPlayers.put(target.getUniqueId(), crashData);
        FoliaCompatUtil.runTaskTimerAsync(this.plugin, object -> {
            handleCrashState(target);
        }, 2L * 60 * 20, 0);

        crashService.crashPlayer(target, method);
    }

    public boolean isCrashed(Player player) {
        return crashedPlayers.containsKey(player.getUniqueId());
    }

    public CrashData getCrashData(Player player) {
        return crashedPlayers.get(player.getUniqueId());
    }

    public void removeCrashedPlayer(Player player) {
        crashedPlayers.remove(player.getUniqueId());
    }

    private void handleCrashState(Player target) {
        if (isCrashed(target)) {
            removeCrashedPlayer(target);
        }
    }
}
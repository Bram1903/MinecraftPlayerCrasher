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

/**
 * Manages operations related to crashing player's game.
 */
public class CrashManager {
    private final ConcurrentHashMap<UUID, CrashData> crashedPlayers = new ConcurrentHashMap<>();

    private final PlayerCrasher plugin;
    private final CrashService crashService;

    /**
     * Creates a CrashManager instance.
     *
     * @param plugin an instance of the plugin
     */
    public CrashManager(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.crashService = new CrashService();
    }

    /**
     * Initiates an operation to crash a player's game.
     *
     * @param sender the command sender
     * @param target the targeted player
     * @param method the used crash method
     */
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


    /**
     * Checks if a player is registered as crashed.
     *
     * @param player the player to check
     * @return true if the player is registered as crashed; false otherwise
     */
    public boolean isCrashed(Player player) {
        return crashedPlayers.containsKey(player.getUniqueId());
    }

    /**
     * Retrieves the crash data for a player.
     *
     * @param player the player whose crash data to retrieve
     * @return the CrashData for the player, or null if the player is not registered as crashed
     */
    public CrashData getCrashData(Player player) {
        return crashedPlayers.get(player.getUniqueId());
    }

    /**
     * Removes a player from the list of crashed players.
     *
     * @param player the player to remove
     */
    public void removeCrashedPlayer(Player player) {
        crashedPlayers.remove(player.getUniqueId());
    }

    /**
     * Handles the state of a crashed player. If a player is registered as crashed,
     * they are removed from the list of crashed players.
     *
     * @param target the player whose crash status to handle
     */
    private void handleCrashState(Player target) {
        if (isCrashed(target)) {
            removeCrashedPlayer(target);
        }
    }
}
package com.deathmotion.playercrasher.managers;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.models.CrashData;
import com.deathmotion.playercrasher.services.CrashService;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages operations related to crashing player's game.
 */
public class CrashManager {
    private final PlayerCrasher plugin;
    private final Random random = new Random();

    private final ConcurrentHashMap<UUID, CrashData> crashedPlayers = new ConcurrentHashMap<>();
    private final CrashService crashService;

    /**
     * Creates a CrashManager instance.
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
        long keepAliveId = random.nextInt(1_000_000_000);
        this.plugin.getLogger().info("Sent KeepAlive ID: " + keepAliveId);

        CrashData crashData = new CrashData();
        crashData.setCrasher(sender);
        crashData.setTarget(target);
        crashData.setMethod(method);
        crashData.setKeepAliveId(keepAliveId);

        crashedPlayers.put(target.getUniqueId(), crashData);
        //crashService.crashPlayer(target, method);

        FoliaScheduler.getAsyncScheduler().runDelayed(plugin, (o) -> {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(target);
            user.sendPacket(new WrapperPlayClientKeepAlive(keepAliveId));
        }, 500, TimeUnit.MILLISECONDS);
    }

    public boolean isCrashed(UUID uuid) {
        return crashedPlayers.containsKey(uuid);
    }

    public CrashData getCrashData(UUID uuid) {
        return crashedPlayers.get(uuid);
    }

    public void removeCrashedPlayer(Player player) {
        crashedPlayers.remove(player.getUniqueId());
    }
}
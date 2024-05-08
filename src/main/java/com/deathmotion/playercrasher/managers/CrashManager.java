package com.deathmotion.playercrasher.managers;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.models.CrashData;
import com.deathmotion.playercrasher.services.CrashService;
import com.deathmotion.playercrasher.util.AdventureCompatUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages operations related to crashing player's game.
 */
public class CrashManager {
    private final PlayerCrasher plugin;
    private final AdventureCompatUtil adventure;

    private final CrashService crashService;
    private final boolean useLegacyWindowConfirmation;

    private final ConcurrentHashMap<UUID, CrashData> crashedPlayers = new ConcurrentHashMap<>();
    private final Random random = new Random();

    /**
     * Creates a CrashManager instance.
     */
    public CrashManager(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.adventure = plugin.getAdventureCompatUtil();

        this.crashService = new CrashService();
        this.useLegacyWindowConfirmation = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_17);
    }

    /**
     * Initiates an operation to crash a player's game.
     *
     * @param sender the command sender
     * @param target the targeted player
     * @param method the used crash method
     */
    public void crashPlayer(CommandSender sender, Player target, CrashMethod method) {
        long transactionId = random.nextInt(1_000_000_000);

        CrashData crashData = new CrashData();
        crashData.setCrasher(sender);
        crashData.setTarget(target);
        crashData.setMethod(method);
        crashData.setKeepAliveId(transactionId);

        crashedPlayers.put(target.getUniqueId(), crashData);

        crashService.crashPlayer(target, method);
        sendConnectionPackets(target, transactionId);
        checkConnectionPackets(target.getUniqueId());
    }

    public boolean isCrashed(UUID uuid) {
        return crashedPlayers.containsKey(uuid);
    }

    public Optional<CrashData> getCrashData(UUID uuid) {
        return Optional.ofNullable(crashedPlayers.get(uuid));
    }

    public void removeCrashedPlayer(UUID uuid) {
        crashedPlayers.remove(uuid);
    }

    private void sendConnectionPackets(Player target, long transactionId) {
        FoliaScheduler.getAsyncScheduler().runDelayed(plugin, (o) -> {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(target);

            user.sendPacket(new WrapperPlayServerKeepAlive(transactionId));

            if (useLegacyWindowConfirmation) {
                user.sendPacket(new WrapperPlayServerWindowConfirmation(0, (short) 0, false));
            } else {
                user.sendPacket(new WrapperPlayServerPing((int) transactionId));
            }
        }, 100, TimeUnit.MILLISECONDS);
    }

    private void checkConnectionPackets(UUID uuid) {
        FoliaScheduler.getAsyncScheduler().runDelayed(plugin, (o) -> {
            CrashData crashData = getCrashData(uuid).orElse(null);

            if (crashData == null) return;

            if (!crashData.isKeepAliveConfirmed() || !crashData.isTransactionConfirmed()) {
                notifyCrashers(crashData);
            }

            removeCrashedPlayer(crashData.getTarget().getUniqueId());
        }, 200, TimeUnit.MILLISECONDS);
    }

    private void notifyCrashers(CrashData crashData) {
        Component notifyComponent = createCrashComponent(crashData);
        CommandSender crasher = crashData.getCrasher();

        if (crasher instanceof Player) {
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission("PlayerCrasher.Alerts") || player.getUniqueId().equals(((Player) crasher).getUniqueId()))
                    .map(player -> PacketEvents.getAPI().getPlayerManager().getUser(player))
                    .forEach(user -> user.sendMessage(notifyComponent));
        } else {
            adventure.broadcastComponent(notifyComponent, "PlayerCrasher.Alerts");
            adventure.sendPlainMessage(crasher, notifyComponent);
        }
    }

    /**
     * Creates a Component containing detailed information about the crash that can be used in a message.
     *
     * @param crashData A data model containing details about the crash
     * @return A Component with a detailed breakdown of the crash
     */
    private Component createCrashComponent(CrashData crashData) {
        Component hoveredComponent = Component.text()
                .append(Component.text("\u25cf"))
                .append(Component.text(" Crash Information", NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD))
                .appendNewline()
                .appendNewline()
                .append(Component.text("Crasher", NamedTextColor.BLUE))
                .append(Component.text(" > ", NamedTextColor.GRAY)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(crashData.getCrasher().getName(), NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Target", NamedTextColor.BLUE))
                .append(Component.text("   > ", NamedTextColor.GRAY)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(crashData.getTarget().getName(), NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Method", NamedTextColor.BLUE))
                .append(Component.text("   > ", NamedTextColor.GRAY)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(
                        crashData.getMethod().toString().substring(0, 1).toUpperCase() +
                                crashData.getMethod().toString().substring(1).toLowerCase(), NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Time", NamedTextColor.BLUE))
                .append(Component.text("      > ", NamedTextColor.GRAY)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(crashData.getFormattedDateTime(), NamedTextColor.GREEN))
                .appendNewline()
                .build();

        return Component.text()
                .append(Component.text(crashData.getTarget().getName()))
                .append(Component.text(" has successfully been crashed!"))
                .color(NamedTextColor.GREEN)
                .hoverEvent(hoveredComponent)
                .build();
    }
}
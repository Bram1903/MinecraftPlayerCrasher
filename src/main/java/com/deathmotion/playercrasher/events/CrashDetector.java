package com.deathmotion.playercrasher.events;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.managers.CrashManager;
import com.deathmotion.playercrasher.models.CrashData;
import io.github.retrooper.packetevents.util.FoliaCompatUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Detects when a player quits the game and checks if a crash has occurred.
 */
@SuppressWarnings("UnnecessaryUnicodeEscape")
public class CrashDetector implements Listener {

    private final PlayerCrasher plugin;
    private final CrashManager crashManager;
    private final BukkitAudiences adventure;

    /**
     * Creates a CrashDetector instance.
     *
     * @param plugin an instance of the plugin
     */
    public CrashDetector(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.crashManager = plugin.getCrashManager();
        this.adventure = plugin.getAdventure();
    }

    /**
     * Handler method for PlayerQuitEvent. Checks if the player who has quit was crashed.
     *
     * @param event An event representing a player quitting the game.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        FoliaCompatUtil.runTaskAsync(this.plugin, () -> {
            Player player = event.getPlayer();

            if (crashManager.isCrashed(player)) {
                CrashData crashData = crashManager.getCrashData(player);
                notifyCrash(crashData);

                crashManager.removeCrashedPlayer(player);
            }
        });
    }

    /**
     * Notifies the crasher that the target player has been successfully crashed
     * and sends the crash details to other players with the "PlayerCrasher.Notify" permission.
     *
     * @param crashData A data model containing details about the crash
     */
    private void notifyCrash(CrashData crashData) {
        Component notifyComponent = createCrashComponent(crashData);
        CommandSender crasher = crashData.getCrasher();
        Audience audienceCrasher = adventure.sender(crasher);

        if (crasher instanceof ConsoleCommandSender) {
            // If the crasher is the console, only send the global message,
            // as the console will always have the required permission
            adventure
                    .permission("PlayerCrasher.Alerts")
                    .sendMessage(notifyComponent);
        } else {
            // Notify the (crasher) of the crash, but filter him out from the global message,
            // as he might have both permissions and would receive the message twice
            audienceCrasher.sendMessage(notifyComponent);

            adventure
                    .permission("PlayerCrasher.Alerts")
                    .filterAudience(p -> !p.equals(audienceCrasher))
                    .sendMessage(notifyComponent);
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
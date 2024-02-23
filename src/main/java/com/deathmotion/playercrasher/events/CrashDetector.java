package com.deathmotion.playercrasher.events;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.managers.CrashManager;
import com.deathmotion.playercrasher.models.CrashData;
import io.github.retrooper.packetevents.util.FoliaCompatUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("UnnecessaryUnicodeEscape")
public class CrashDetector implements Listener {

    private final PlayerCrasher plugin;
    private final CrashManager crashManager;
    private final BukkitAudiences adventure;

    public CrashDetector(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.crashManager = plugin.getCrashManager();
        this.adventure = plugin.getAdventure();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        FoliaCompatUtil.runTaskAsync(this.plugin, () -> {
            Player player = event.getPlayer();

            if (crashManager.isCrashed(player)) {
                CrashData crashData = crashManager.getCrashData(player);

                adventure.permission("PlayerCrasher.Notify").sendMessage(createCrashComponent(crashData));
                crashManager.removeCrashedPlayer(player);
            }
        });
    }

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
                .append(Component.text(crashData.getMethod().toString(), NamedTextColor.GREEN))
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
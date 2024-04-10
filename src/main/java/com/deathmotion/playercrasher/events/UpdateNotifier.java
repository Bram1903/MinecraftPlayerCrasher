package com.deathmotion.playercrasher.events;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.util.folia.FoliaCompatUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for notifying when an update is available.
 */
public class UpdateNotifier implements Listener {

    private final PlayerCrasher plugin;
    private final BukkitAudiences adventure;
    private final Component updateComponent;

    /**
     * Constructor for the UpdateNotifier
     *
     * @param plugin        instance of PlayerCrasher plugin
     * @param latestVersion the latest version of the plugin
     */
    public UpdateNotifier(PlayerCrasher plugin, String latestVersion) {
        this.plugin = plugin;
        this.adventure = plugin.getAdventure();

        // preparing the update notification message
        this.updateComponent = Component.text()
                .append(Component.text("[PlayerCrasher] ", NamedTextColor.RED)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text("Version " + latestVersion + " is ", NamedTextColor.GREEN))
                .append(Component.text("now available", NamedTextColor.GREEN)
                        .decorate(TextDecoration.UNDERLINED)
                        .hoverEvent(HoverEvent.showText(Component.text("Click to download", NamedTextColor.GREEN)))
                        .clickEvent(ClickEvent.openUrl("https://github.com/Bram1903/MinecraftPlayerCrasher/releases/latest/")))
                .append(Component.text("!", NamedTextColor.GREEN))
                .build();
    }

    /**
     * Event listener for player joining the game
     *
     * @param event encapsulates information about the player join event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("PlayerCrasher.UpdateNotify")) {
            FoliaCompatUtil.getAsyncScheduler().runDelayed(plugin, (o) -> {
                adventure.player(player).sendMessage(updateComponent);
            }, 2, TimeUnit.SECONDS);
        }
    }
}
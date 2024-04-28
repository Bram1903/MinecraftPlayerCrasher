package com.deathmotion.playercrasher.commands;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.util.AdventureCompatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Main command executor for the PlayerCrasher plugin.
 * This class handles setup, command registration and execution of all PlayerCrasher plugin commands.
 */
@SuppressWarnings("UnnecessaryUnicodeEscape")
public class PCCommand implements CommandExecutor {
    private final PlayerCrasher plugin;
    private final AdventureCompatUtil adventure;

    private Component pcComponent;

    /**
     * Constructor for main command executor.
     *
     * @param plugin The instance of the main plugin class to use.
     */
    public PCCommand(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.adventure = plugin.getAdventureCompatUtil();

        initPcComponent();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        adventure.sendComponent(sender, pcComponent);
        return true;
    }

    /**
     * Initialises the PlayerCrasher plugin message component.
     */
    private void initPcComponent() {
        pcComponent = Component.text()
                .append(Component.text("\u25cf", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(" Running ", NamedTextColor.GRAY))
                .append(Component.text("PlayerCrasher", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(" v" + plugin.getDescription().getVersion(), NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(" by ", NamedTextColor.GRAY))
                .append(Component.text("Bram", NamedTextColor.GREEN))
                .build();
    }
}
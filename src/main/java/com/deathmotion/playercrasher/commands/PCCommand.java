/*
 * This file is part of PlayerCrasher - https://github.com/Bram1903/MinecraftPlayerCrasher
 * Copyright (C) 2024 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
        if (!sender.hasPermission("PlayerCrasher.Crash")) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return false;
        }

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
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
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.managers.CrashManager;
import com.deathmotion.playercrasher.util.AdventureCompatUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CrashCommand implements CommandExecutor, TabExecutor {
    private final PlayerCrasher plugin;
    private final CrashManager crashManager;
    private final AdventureCompatUtil adventure;

    public CrashCommand(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.crashManager = plugin.getCrashManager();
        this.adventure = plugin.getAdventureCompatUtil();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("PlayerCrasher.Crash")) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return false;
        }

        if (args.length == 0) {
            adventure.sendComponent(sender, Component.text("Usage: /crash <player> [method]", NamedTextColor.RED));
            return false;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            adventure.sendComponent(sender, Component.text("Player not found.", NamedTextColor.RED));
            return false;
        }

        if (target == sender) {
            adventure.sendComponent(sender, Component.text("You cannot crash yourself.", NamedTextColor.RED));
            return false;
        }

        if (target.hasPermission("PlayerCrasher.Bypass")) {
            adventure.sendComponent(sender, Component.text("This player is immune to crashing.", NamedTextColor.RED));
            return false;
        }

        CrashMethod method = CrashMethod.EXPLOSION;

        if (args.length == 1) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(target);
            if (user.getClientVersion().isOlderThan(ClientVersion.V_1_12)) {
                method = CrashMethod.POSITION;
            }
        } else {
            try {
                method = CrashMethod.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                adventure.sendComponent(sender, Component.text("Invalid crash method.", NamedTextColor.RED));
                return false;
            }
        }

        Component crasherComponent = Component.text()
                .append(Component.text("Attempting to crash " + target.getName() + "...", NamedTextColor.GREEN))
                .build();

        adventure.sendComponent(sender, crasherComponent);
        crashManager.crashPlayer(sender, target, method);

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                // We don't want to suggest ourselves as a target
                if (player == sender) continue;

                suggestions.add(player.getName());
            }
        } else if (args.length == 2) {
            for (CrashMethod method : CrashMethod.values()) {
                suggestions.add(method.getProperName());
            }
        }

        return suggestions;
    }
}
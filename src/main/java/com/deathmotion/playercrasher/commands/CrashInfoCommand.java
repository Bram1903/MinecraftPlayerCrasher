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
import com.deathmotion.playercrasher.managers.CrashManager;
import com.deathmotion.playercrasher.util.AdventureCompatUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CrashInfoCommand implements CommandExecutor {
    private final CrashManager crashManager;
    private final AdventureCompatUtil adventure;

    public CrashInfoCommand(PlayerCrasher plugin) {
        this.crashManager = plugin.getCrashManager();
        this.adventure = plugin.getAdventureCompatUtil();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("PlayerCrasher.CrashInfo")) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return false;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must specify a player when running this command from the console.");
                return false;
            } else {
                User user = PacketEvents.getAPI().getPlayerManager().getUser(sender);
                String clientBrand = crashManager.getClientBrand(user.getUUID()).orElse(null);

                if (clientBrand == null) {
                    user.sendMessage(Component.text("We haven't been able to retrieve your client brand.", NamedTextColor.RED));
                    return false;
                }

                Component crashInfoComponent = Component.text()
                        .append(Component.text("You are running ", NamedTextColor.GRAY))
                        .append(Component.text(clientBrand, NamedTextColor.GOLD)
                                .decorate(TextDecoration.BOLD))
                        .append(Component.text(" on Minecraft version ", NamedTextColor.GRAY))
                        .append(Component.text(user.getClientVersion().getReleaseName(), NamedTextColor.GOLD)
                                .decorate(TextDecoration.BOLD))
                        .append(Component.text(".", NamedTextColor.GRAY))
                        .build();

                user.sendMessage(crashInfoComponent);
                return true;
            }
        }

        Player playerToCheck = Bukkit.getPlayer(args[0]);

        if (playerToCheck == null) {
            adventure.sendComponent(sender, Component.text("Player not found.", NamedTextColor.RED));
            return false;
        }

        User userToCheck = PacketEvents.getAPI().getPlayerManager().getUser(playerToCheck);
        String clientBrand = crashManager.getClientBrand(userToCheck.getUUID()).orElse(null);

        if (clientBrand == null) {
            adventure.sendComponent(sender, Component.text("We haven't been able to retrieve the client brand of " + playerToCheck.getName() + ".", NamedTextColor.RED));
            return false;
        }

        Component crashInfoComponent = Component.text()
                .append(Component.text(playerToCheck.getName(), NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(" is running ", NamedTextColor.GRAY))
                .append(Component.text(clientBrand, NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(" on Minecraft version ", NamedTextColor.GRAY))
                .append(Component.text(userToCheck.getClientVersion().getReleaseName(), NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(".", NamedTextColor.GRAY))
                .build();

        adventure.sendComponent(sender, crashInfoComponent);
        return true;
    }
}
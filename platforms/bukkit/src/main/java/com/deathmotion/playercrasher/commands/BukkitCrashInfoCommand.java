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

import com.deathmotion.playercrasher.PCBukkit;
import com.deathmotion.playercrasher.util.CommandUtil;
import com.deathmotion.playercrasher.util.MessageSender;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class BukkitCrashInfoCommand implements CommandExecutor, TabCompleter {
    private final PCBukkit plugin;
    private final MessageSender messageSender;

    public BukkitCrashInfoCommand(PCBukkit plugin) {
        this.plugin = plugin;
        this.messageSender = plugin.getPc().messageSender;

        plugin.getCommand("CrashInfo").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.hasPermission("PlayerCrasher.CrashInfo")) {
            messageSender.sendMessages(sender, CommandUtil.noPermission);
            return false;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                plugin.getPc().sendConsoleMessage(CommandUtil.specifyPlayer);
                return false;
            } else {
                User user = PacketEvents.getAPI().getPlayerManager().getUser(sender);
                String clientBrand = plugin.getPc().getClientBrand(user.getUUID());

                user.sendMessage(CommandUtil.personalBrand(clientBrand, user.getClientVersion().getReleaseName()));
                return true;
            }
        }

        Player playerToCheck = Bukkit.getPlayer(args[0]);

        if (playerToCheck == null) {
            messageSender.sendMessages(sender, CommandUtil.playerNotFound);
            return false;
        }

        User userToCheck = PacketEvents.getAPI().getPlayerManager().getUser(playerToCheck);
        String clientBrand = plugin.getPc().getClientBrand(userToCheck.getUUID());
        ClientVersion clientVersion = userToCheck.getClientVersion();

        messageSender.sendMessages(sender, CommandUtil.playerBrand(playerToCheck.getName(), clientBrand, clientVersion.getReleaseName()));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}

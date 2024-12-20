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
import com.deathmotion.playercrasher.data.CommonSender;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.util.CommandUtil;
import com.deathmotion.playercrasher.util.BukkitMessageSender;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BukkitCrashCommand implements CommandExecutor, TabExecutor {

    private final PCBukkit plugin;
    private final BukkitMessageSender bukkitMessageSender;

    public BukkitCrashCommand(PCBukkit plugin) {
        this.plugin = plugin;
        this.bukkitMessageSender = plugin.getPc().bukkitMessageSender;

        plugin.getCommand("Crash").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("PlayerCrasher.Crash")) {
            bukkitMessageSender.sendMessages(sender, CommandUtil.NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            bukkitMessageSender.sendMessages(sender, CommandUtil.INVALID_COMMAND);
            return false;
        }

        Player targetPlayer = plugin.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            bukkitMessageSender.sendMessages(sender, CommandUtil.PLAYER_NOT_FOUND);
            return false;
        }

        User target = PacketEvents.getAPI().getPlayerManager().getUser(targetPlayer);
        if (target == null) {
            bukkitMessageSender.sendMessages(sender, CommandUtil.PLAYER_NOT_FOUND);
            return false;
        }

        if (targetPlayer == sender) {
            bukkitMessageSender.sendMessages(sender, CommandUtil.SELF_CRASH);
            return false;
        }

        if (targetPlayer.hasPermission("PlayerCrasher.Bypass")) {
            bukkitMessageSender.sendMessages(sender, CommandUtil.PLAYER_BYPASS);
            return false;
        }

        CrashMethod method = CrashMethod.EXPLOSION;

        if (args.length == 1) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(targetPlayer);
            if (user.getClientVersion().isOlderThan(ClientVersion.V_1_12)) {
                method = CrashMethod.POSITION;
            }
        } else {
            try {
                method = CrashMethod.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                bukkitMessageSender.sendMessages(sender, CommandUtil.INVALID_METHOD);
                return false;
            }
        }

        bukkitMessageSender.sendMessages(sender, CommandUtil.crashSent(target.getName()));
        plugin.getPc().crashPlayer(createCommonUser(sender), target, method);

        return true;
    }

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

    private CommonSender createCommonUser(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            CommonSender commonSender = new CommonSender();
            commonSender.setUuid(player.getUniqueId());
            commonSender.setName(player.getName());

            return commonSender;
        } else {
            CommonSender commonSender = new CommonSender();
            commonSender.setUuid(null);
            commonSender.setName("Console");
            commonSender.setConsole(true);

            return commonSender;
        }
    }
}

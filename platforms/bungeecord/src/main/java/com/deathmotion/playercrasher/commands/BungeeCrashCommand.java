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

import com.deathmotion.playercrasher.PCBungee;
import com.deathmotion.playercrasher.util.MessageSender;
import com.deathmotion.playercrasher.data.CommonSender;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.util.CommandUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class BungeeCrashCommand extends Command implements TabExecutor {

    private final PCBungee plugin;
    private final MessageSender messageSender;

    public BungeeCrashCommand(PCBungee plugin) {
        super("Crash", "PlayerCrasher.Crash", "");

        this.plugin = plugin;
        this.messageSender = plugin.getPc().messageSender;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("PlayerCrasher.Crash")) {
            messageSender.sendMessages(sender, CommandUtil.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            messageSender.sendMessages(sender, CommandUtil.INVALID_COMMAND);
            return;
        }

        ProxiedPlayer targetPlayer = plugin.getProxy().getPlayer(args[0]);
        if (targetPlayer == null) {
            messageSender.sendMessages(sender, CommandUtil.PLAYER_NOT_FOUND);
            return;
        }

        User target = PacketEvents.getAPI().getPlayerManager().getUser(targetPlayer);
        if (target == null) {
            messageSender.sendMessages(sender, CommandUtil.PLAYER_NOT_FOUND);
            return;
        }

        if (targetPlayer == sender) {
            messageSender.sendMessages(sender, CommandUtil.SELF_CRASH);
            return;
        }

        if (targetPlayer.hasPermission("PlayerCrasher.Bypass")) {
            messageSender.sendMessages(sender, CommandUtil.PLAYER_BYPASS);
            return;
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
                messageSender.sendMessages(sender, CommandUtil.INVALID_METHOD);
                return;
            }
        }

        messageSender.sendMessages(sender, CommandUtil.crashSent(target.getName()));
        plugin.getPc().crashPlayer(createCommonUser(sender), target, method);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
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
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
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


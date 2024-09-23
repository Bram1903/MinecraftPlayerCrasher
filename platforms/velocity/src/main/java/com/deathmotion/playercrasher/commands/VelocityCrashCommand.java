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

import com.deathmotion.playercrasher.PCVelocity;
import com.deathmotion.playercrasher.data.CommonSender;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.util.CommandUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VelocityCrashCommand implements SimpleCommand {
    private final PCVelocity plugin;
    private final ProxyServer proxy;

    public VelocityCrashCommand(PCVelocity plugin) {
        this.plugin = plugin;
        this.proxy = plugin.getPc().getPlatform();

        CommandMeta commandMeta = this.proxy.getCommandManager().metaBuilder("crash")
                .build();
        this.proxy.getCommandManager().register(commandMeta, this);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("PlayerCrasher.Crash")) {
            source.sendMessage(CommandUtil.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            source.sendMessage(CommandUtil.INVALID_COMMAND);
            return;
        }

        Player targetPlayer = proxy.getPlayer(args[0]).orElse(null);
        if (targetPlayer == null) {
            source.sendMessage(CommandUtil.PLAYER_NOT_FOUND);
            return;
        }

        User target = PacketEvents.getAPI().getPlayerManager().getUser(targetPlayer);
        if (target == null) {
            source.sendMessage(CommandUtil.PLAYER_NOT_FOUND);
            return;
        }

        if (targetPlayer == source) {
            source.sendMessage(CommandUtil.SELF_CRASH);
            return;
        }

        if (targetPlayer.hasPermission("PlayerCrasher.Bypass")) {
            source.sendMessage(CommandUtil.PLAYER_BYPASS);
            return;
        }

        CrashMethod method = CrashMethod.EXPLOSION;

        if (args.length == 1) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(targetPlayer);
            if (user.getClientVersion().isOlderThan(ClientVersion.V_1_12)) {
                method = CrashMethod.POSITION;
            }
        } else {
            for (CrashMethod crashMethod : CrashMethod.values()) {
                if (crashMethod.getProperName().equalsIgnoreCase(args[1])) {
                    method = crashMethod;
                    break;
                }
            }
        }

        source.sendMessage(CommandUtil.crashSent(target.getName()));
        plugin.getPc().crashPlayer(createCommonUser(source), target, method);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            if (source instanceof Player) {
                Player player = (Player) source;
                for (Player onlinePlayer : proxy.getAllPlayers()) {
                    if (onlinePlayer.getUniqueId() != player.getUniqueId()) {
                        suggestions.add(onlinePlayer.getUsername());
                    }
                }
            } else {
                suggestions.addAll(proxy.getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toList()));
            }
        } else if (args.length == 2) {
            for (CrashMethod method : CrashMethod.values()) {
                suggestions.add(method.getProperName());
            }
        }

        return suggestions;
    }

    private CommonSender createCommonUser(CommandSource sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            CommonSender commonSender = new CommonSender();
            commonSender.setUuid(player.getUniqueId());
            commonSender.setName(player.getUsername());

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

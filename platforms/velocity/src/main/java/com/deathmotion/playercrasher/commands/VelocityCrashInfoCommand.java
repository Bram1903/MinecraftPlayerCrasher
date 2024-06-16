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
import com.deathmotion.playercrasher.util.CommandUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.List;
import java.util.stream.Collectors;

public class VelocityCrashInfoCommand implements SimpleCommand {
    private final PCVelocity plugin;
    private final ProxyServer proxy;

    public VelocityCrashInfoCommand(PCVelocity plugin) {
        this.plugin = plugin;
        this.proxy = plugin.getPc().getPlatform();

        CommandMeta commandMeta = this.proxy.getCommandManager().metaBuilder("crashinfo")
                .aliases("brand")
                .build();
        this.proxy.getCommandManager().register(commandMeta, this);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("PlayerCrasher.Crash")) {
            source.sendMessage(CommandUtil.noPermission);
            return;
        }

        if (args.length == 0) {
            if (!(source instanceof Player)) {
                source.sendMessage(CommandUtil.specifyPlayer);
                return;
            } else {
                User user = PacketEvents.getAPI().getPlayerManager().getUser(source);
                String clientBrand = plugin.getPc().getClientBrand(user.getUUID());

                user.sendMessage(CommandUtil.personalBrand(clientBrand, user.getClientVersion().getReleaseName()));
                return;
            }
        }

        Player playerToCheck = proxy.getPlayer(args[0]).orElse(null);
        if (playerToCheck == null) {
            source.sendMessage(CommandUtil.playerNotFound);
            return;
        }

        User userToCheck = PacketEvents.getAPI().getPlayerManager().getUser(playerToCheck);
        String clientBrand = plugin.getPc().getClientBrand(userToCheck.getUUID());
        ClientVersion clientVersion = userToCheck.getClientVersion();

        source.sendMessage(CommandUtil.playerBrand(playerToCheck.getUsername(), clientBrand, clientVersion.getReleaseName()));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return plugin.getPc().getPlatform().getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toList());
    }
}

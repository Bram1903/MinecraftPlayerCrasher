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
import com.deathmotion.playercrasher.Util.MessageSender;
import com.deathmotion.playercrasher.util.CommandUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.stream.Collectors;

public class BungeeCrashInfoCommand extends Command implements TabExecutor {

    private final PCBungee plugin;

    public BungeeCrashInfoCommand(PCBungee plugin) {
        super("CrashInfo", "PlayerCrasher.CrashInfo", "brand");

        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("PlayerCrasher.CrashInfo")) {
            MessageSender.sendMessages(sender, CommandUtil.noPermission);
            return;
        }

        if (args.length == 0) {
            if (!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(LegacyComponentSerializer.legacySection().serialize(CommandUtil.specifyPlayer));
                return;
            } else {
                User user = PacketEvents.getAPI().getPlayerManager().getUser(sender);
                String clientBrand = plugin.getPc().getClientBrand(user.getUUID());

                user.sendMessage(CommandUtil.personalBrand(clientBrand, user.getClientVersion().getReleaseName()));
                return;
            }
        }

        ProxiedPlayer playerToCheck = plugin.getProxy().getPlayer(args[0]);

        if (playerToCheck == null) {
            MessageSender.sendMessages(sender, CommandUtil.playerNotFound);
            return;
        }

        User userToCheck = PacketEvents.getAPI().getPlayerManager().getUser(playerToCheck);
        String clientBrand = plugin.getPc().getClientBrand(userToCheck.getUUID());
        ClientVersion clientVersion = userToCheck.getClientVersion();

        MessageSender.sendMessages(sender, CommandUtil.playerBrand(playerToCheck.getName(), clientBrand, clientVersion.getReleaseName()));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return plugin.getProxy().getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toList());
    }
}

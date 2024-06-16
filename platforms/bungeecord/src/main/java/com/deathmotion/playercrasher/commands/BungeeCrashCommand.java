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
import com.deathmotion.playercrasher.data.CommonSender;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.util.CommandUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class BungeeCrashCommand extends Command implements TabExecutor {

    private final PCBungee plugin;

    public BungeeCrashCommand(PCBungee plugin) {
        super("Crash", "PlayerCrasher.Crash", "");

        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("PlayerCrasher.Crash")) {
            sendMessages(sender, CommandUtil.noPermission);
            return;
        }

        if (args.length == 0) {
            sendMessages(sender, CommandUtil.invalidCommand);
            return;
        }

        ProxiedPlayer targetPlayer = plugin.getProxy().getPlayer(args[0]);
        if (targetPlayer == null) {
            sendMessages(sender, CommandUtil.playerNotFound);
            return;
        }

        User target = PacketEvents.getAPI().getPlayerManager().getUser(targetPlayer);
        if (target == null) {
            sendMessages(sender, CommandUtil.playerNotFound);
            return;
        }

        if (targetPlayer == sender) {
            sendMessages(sender, CommandUtil.selfCrash);
            return;
        }

        if (targetPlayer.hasPermission("PlayerCrasher.Bypass")) {
            sendMessages(sender, CommandUtil.playerBypass);
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
                sendMessages(sender, CommandUtil.invalidMethod);
                return;
            }
        }

        sendMessages(sender, CommandUtil.crashSent(target.getName()));
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

    private void sendMessages(CommandSender sender, Component message) {
        if (sender instanceof ProxiedPlayer) {
            PacketEvents.getAPI().getPlayerManager().getUser(sender).sendMessage(message);
        } else {
            sender.sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
        }
    }

    private CommonSender createCommonUser(CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            return new CommonSender(player.getUniqueId(), player.getName(), false);
        } else {
            return new CommonSender(null, "Console", true);
        }
    }
}


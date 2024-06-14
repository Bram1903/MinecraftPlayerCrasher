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

import com.deathmotion.playercrasher.PCPlatform;
import com.deathmotion.playercrasher.data.CommonUser;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.interfaces.Adventure;
import com.deathmotion.playercrasher.managers.CrashManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public class AbstractCrashCommand<P> {

    private final PCPlatform<P> platform;
    private final Adventure adventure;
    private final CrashManager<?> crashManager;

    public AbstractCrashCommand(PCPlatform<P> platform) {
        this.platform = platform;
        this.adventure = platform.getAdventure();
        this.crashManager = platform.getCrashManager();
    }

    public void execute(CommonUser sender, UUID target, String[] args) {
        if (sender == null) return;

        if (!this.platform.hasPermission(sender.getUuid(), "PlayerCrasher.Crash")) {
            sendMessage(sender, Component.text("You don't have permission to use this command.", NamedTextColor.RED));
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, Component.text("Usage: /crash <player> [method]", NamedTextColor.RED));
            return;
        }

        User targetUser = getUser(target);
        if (targetUser == null) {
            sendMessage(sender, Component.text("Player not found.", NamedTextColor.RED));
            return;
        }

        if (sender.getUuid() == targetUser.getUUID()) {
            sendMessage(sender, Component.text("You can't crash yourself.", NamedTextColor.RED));
            return;
        }

        if (this.platform.hasPermission(targetUser.getUUID(), "PlayerCrasher.Bypass")) {
            sendMessage(sender, Component.text("This player is immune to crashing.", NamedTextColor.RED));
            return;
        }

        CrashMethod method = CrashMethod.EXPLOSION;

        if (args.length == 1) {
            if (targetUser.getClientVersion().isOlderThan(ClientVersion.V_1_12)) {
                method = CrashMethod.POSITION;
            }
        } else {
            try {
                method = CrashMethod.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                sendMessage(sender, Component.text("Invalid crash method.", NamedTextColor.RED));

                return;
            }
        }

        Component crasherComponent = Component.text()
                .append(Component.text("Attempting to crash " + targetUser.getName() + "...", NamedTextColor.GREEN))
                .build();

        sendMessage(sender, crasherComponent);
        crashManager.crash(sender, targetUser, method);
    }

    private User getUser(UUID target) {
        Object channel = PacketEvents.getAPI().getProtocolManager().getChannel(target);

        if (channel != null) {
            return PacketEvents.getAPI().getProtocolManager().getUser(channel);
        }

        return null;
    }

    private void sendMessage(CommonUser sender, Component message) {
        if (sender.isConsole()) {
            adventure.sendConsoleMessage(message);
        } else {
            adventure.sendMessage(message, sender.getUuid());
        }
    }
}

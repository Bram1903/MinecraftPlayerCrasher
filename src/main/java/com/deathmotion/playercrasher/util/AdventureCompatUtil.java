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

package com.deathmotion.playercrasher.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility class for managing player sending and broadcasting messages via the Adventure API,
 * with compatibility for Bukkit's CommandSender.
 */
public final class AdventureCompatUtil {

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-ORX]|\\\u25cf");

    /**
     * Sends a text component to a CommandSender.
     *
     * @param sender    the CommandSender to send the component to.
     * @param component the Component to send.
     */
    public void sendComponent(CommandSender sender, Component component) {
        if (sender instanceof Player) {
            getUser(((Player) sender).getUniqueId()).ifPresent(user -> user.sendMessage(component));
        } else {
            sendPlainMessage(sender, component);
        }
    }

    /**
     * Broadcasts a text component to all Players with a specific permission.
     * If the permission is null, the component is broadcast to all Players.
     *
     * @param component  the Component to broadcast.
     * @param permission the permission required to receive the broadcast, or null to broadcast to all Players.
     */
    public void broadcastComponent(Component component, @Nullable String permission) {
        if (permission != null) {
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission(permission))
                    .map(player -> PacketEvents.getAPI().getPlayerManager().getUser(player))
                    .forEach(user -> user.sendMessage(component));
        } else {
            getUsers().forEach(user -> user.sendMessage(component));
        }
    }

    /**
     * Sends a plain text message to a CommandSender.
     *
     * @param sender    the CommandSender to send the message to.
     * @param component the Component to convert to plain text and send.
     */
    public void sendPlainMessage(CommandSender sender, Component component) {
        sender.sendMessage(STRIP_COLOR_PATTERN
                .matcher(LegacyComponentSerializer.legacyAmpersand().serialize(component))
                .replaceAll("")
                .trim());
    }

    /**
     * Retrieves a list of Users from the PacketEvents API.
     *
     * @return a Collection of Users.
     */
    private Collection<User> getUsers() {
        return PacketEvents.getAPI().getProtocolManager().getUsers();
    }

    /**
     * Retrieves a PacketEvents User by UUID.
     *
     * @param playerUUID the UUID of the Player.
     * @return an Optional containing the User if one was found, or an empty Optional if no User was found.
     */
    private Optional<User> getUser(UUID playerUUID) {
        return PacketEvents.getAPI()
                .getProtocolManager()
                .getUsers()
                .stream()
                .filter(user -> user != null && user.getUUID() != null)
                .filter(user -> user.getUUID().equals(playerUUID))
                .findFirst();
    }
}
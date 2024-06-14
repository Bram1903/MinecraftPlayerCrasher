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

package com.deathmotion.playercrasher.data;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessageLegacy;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_16;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class CommonUser {
    @Getter
    private final UUID uuid;
    @Setter
    private String clientBrand;
    @Getter
    private final String name;

    private final User user;

    private final Object channel;

    public CommonUser(UUID uuid) {
        channel = PacketEvents.getAPI().getProtocolManager().getChannel(uuid);
        if (channel == null) throw new IllegalArgumentException("Channel is null");

        User user = PacketEvents.getAPI().getProtocolManager().getUser(uuid);
        if (user == null) throw new IllegalArgumentException("User is null");

        this.uuid = uuid;
        this.name = user.getName();
        this.user = user;
    }

    public String getClientBrand() {
        if (clientBrand != null) {
            return clientBrand;
        }
        else {
            return "Unknown Client";
        }
    }

    public void sendMessage(Component component) {
        ServerVersion version = PacketEvents.getAPI().getInjector().isProxy() ? user.getClientVersion().toServerVersion() : PacketEvents.getAPI().getServerManager().getVersion();
        PacketWrapper<?> chatPacket;

        if (version.isNewerThanOrEquals(ServerVersion.V_1_19)) {
            chatPacket = new WrapperPlayServerSystemChatMessage(false, component);
        } else {
            ChatMessage message;
            if (version.isNewerThanOrEquals(ServerVersion.V_1_16)) {
                message = new ChatMessage_v1_16(component, ChatTypes.CHAT, new UUID(0L, 0L));
            } else {
                message = new ChatMessageLegacy(component, ChatTypes.CHAT);
            }
            chatPacket = new WrapperPlayServerChatMessage(message);
        }

        PacketEvents.getAPI().getProtocolManager().sendPacket(channel, chatPacket);
    }
}

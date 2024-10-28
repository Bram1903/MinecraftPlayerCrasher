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

package com.deathmotion.playercrasher.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserTracker extends PacketListenerAbstract {

    private final ConcurrentHashMap<UUID, String> clientBrands = new ConcurrentHashMap<>();

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.PLUGIN_MESSAGE && event.getPacketType() != PacketType.Configuration.Client.PLUGIN_MESSAGE)
            return;

        WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);

        String channelName = packet.getChannelName();
        if (!channelName.equalsIgnoreCase("minecraft:brand") && !channelName.equals("MC|Brand")) return;

        byte[] data = packet.getData();
        if (data.length > 64 || data.length == 0) return;

        byte[] minusLength = new byte[data.length - 1];
        System.arraycopy(data, 1, minusLength, 0, minusLength.length);
        String brand = new String(minusLength).replace(" (Velocity)", "");

        if (brand.isEmpty()) brand = "Unknown";
        clientBrands.put(event.getUser().getUUID(), brand);
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        UUID userUUID = event.getUser().getUUID();
        if (userUUID == null) return;

        clientBrands.remove(userUUID);
    }

    public String getClientBrand(UUID uuid) {
        return clientBrands.getOrDefault(uuid, "Unknown");
    }
}

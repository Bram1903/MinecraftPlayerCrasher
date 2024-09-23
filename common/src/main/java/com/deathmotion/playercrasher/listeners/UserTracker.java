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

import com.deathmotion.playercrasher.data.CommonSender;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.event.UserLoginEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserTracker extends PacketListenerAbstract {

    private final ConcurrentHashMap<UUID, CommonSender> users = new ConcurrentHashMap<>();

    @Override
    public void onUserLogin(UserLoginEvent event) {
        User user = event.getUser();
        if (user.getUUID() == null) return;

        createOrUpdateCommonSender(user, null);
    }

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
        createOrUpdateCommonSender(event.getUser(), brand);
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        UUID userUUID = event.getUser().getUUID();
        if (userUUID == null) return;

        users.remove(userUUID);
    }

    public Optional<CommonSender> getUser(UUID uuid) {
        return Optional.ofNullable(users.get(uuid));
    }

    public String getClientBrand(UUID uuid) {
        return getUser(uuid).map(CommonSender::getClientBrand).orElse("Unknown");
    }

    private void createOrUpdateCommonSender(User user, @Nullable String brand) {
        UUID userUUID = user.getUUID();
        users.compute(userUUID, (uuid, existing) -> {
            String clientBrand = (existing != null && existing.getClientBrand() != null)
                    ? existing.getClientBrand() // Keep existing brand if it's not null
                    : (brand != null ? brand : "Unknown"); // Use provided brand or default to "Unknown"

            CommonSender commonSender = new CommonSender();
            commonSender.setUuid(userUUID);
            commonSender.setName(user.getName());
            commonSender.setClientBrand(clientBrand);
            return commonSender;
        });
    }
}

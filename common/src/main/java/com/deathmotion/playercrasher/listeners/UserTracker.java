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
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.event.UserLoginEvent;
import com.github.retrooper.packetevents.protocol.player.User;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserTracker implements PacketListener {

    private final ConcurrentHashMap<UUID, CommonSender> users = new ConcurrentHashMap<>();

    @Override
    public void onUserLogin(UserLoginEvent event) {
        User user = event.getUser();

        CommonSender commonUser = new CommonSender(user.getUUID(), user.getName());
        users.putIfAbsent(user.getUUID(), commonUser);
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
        return getUser(uuid).map(CommonSender::getClientBrand).orElse("Unknown Client");
    }

    public void setClientBrand(UUID uuid, String brand) {
        getUser(uuid).ifPresent(user -> user.setClientBrand(brand));
    }
}

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

package com.deathmotion.playercrasher.managers;

import com.deathmotion.playercrasher.data.CommonUser;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.event.UserLoginEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager implements PacketListener {

    private final ConcurrentHashMap<UUID, CommonUser> users = new ConcurrentHashMap<>();

    @Override
    public void onUserLogin(UserLoginEvent event) {
        UUID userUUID = event.getUser().getUUID();
        if (userUUID == null) return;

        users.putIfAbsent(userUUID, new CommonUser(userUUID));
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        UUID userUUID = event.getUser().getUUID();
        if (userUUID == null) return;

        users.remove(userUUID);
    }

    public void setClientBrand(UUID userUUID, String clientBrand) {
        CommonUser user = users.get(userUUID);

        if (user != null) {
            user.setClientBrand(clientBrand);
        }
    }

    public CommonUser getUser(UUID userUUID) {
        return users.get(userUUID);
    }

    public List<CommonUser> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}

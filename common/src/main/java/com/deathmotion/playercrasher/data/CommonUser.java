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

import com.deathmotion.playercrasher.PCPlatform;
import com.deathmotion.playercrasher.listeners.UserTracker;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public class CommonUser<P> {
    private final PCPlatform<P> platform;
    private final UserTracker userTracker;

    private final User user;
    private final boolean isConsole;

    public CommonUser(PCPlatform<P> platform, User user) {
        this.platform = platform;
        this.userTracker = platform.getUserTracker();

        this.user = user;
        this.isConsole = user == null;
    }

    public void sendMessage(Component message) {
        if (isConsole) {
            platform.sendConsoleMessage(message);
        } else {
            user.sendMessage(message);
        }
    }

    public boolean hasPermission(String permission) {
        return isConsole || platform.hasPermission(user.getUUID(), permission);
    }

    public String getClientBrand() {
        return isConsole ? "Console" : userTracker.getClientBrand(user.getUUID());
    }
}

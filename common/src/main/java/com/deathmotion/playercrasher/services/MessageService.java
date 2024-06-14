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

package com.deathmotion.playercrasher.services;

import com.deathmotion.playercrasher.PCPlatform;
import com.deathmotion.playercrasher.data.CommonUser;
import com.deathmotion.playercrasher.data.CrashData;
import com.deathmotion.playercrasher.util.ComponentCreator;
import net.kyori.adventure.text.Component;

public class MessageService<P> {

    private final PCPlatform<P> platform;

    public MessageService(PCPlatform<P> platform) {
        this.platform = platform;
    }

    public void notifyCrashers(CrashData crashData) {
        CommonUser crasher = crashData.getCrasher();
        Component notifyComponent = ComponentCreator.createCrashComponent(crashData);
        handleSenderMessage(crasher, notifyComponent);
    }

    public void notifyFailedCrash(CrashData crashData) {
        CommonUser crasher = crashData.getCrasher();
        Component notifyComponent = ComponentCreator.createFailedCrashComponent(crashData);
        handleSenderMessage(crasher, notifyComponent);
    }

    private void handleSenderMessage(CommonUser sender, Component message) {
        if (sender.isConsole()) {
            platform.broadcastComponent(message, "PlayerCrasher.Alerts", null);
        }
        else {
            platform.broadcastComponent(message, "PlayerCrasher.Alerts", sender.getUuid());
        }
    }
}

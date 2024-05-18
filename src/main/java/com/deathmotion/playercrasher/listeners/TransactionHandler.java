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

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.managers.CrashManager;
import com.deathmotion.playercrasher.models.CrashData;
import com.deathmotion.playercrasher.util.AdventureCompatUtil;
import com.deathmotion.playercrasher.util.ComponentCreator;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPong;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;

public class TransactionHandler extends PacketListenerAbstract {
    private final CrashManager crashManager;
    private final AdventureCompatUtil adventure;

    private final PacketTypeCommon keepAlive = PacketType.Play.Client.KEEP_ALIVE;
    private final PacketTypeCommon pong = PacketType.Play.Client.PONG;
    private final PacketTypeCommon transaction = PacketType.Play.Client.WINDOW_CONFIRMATION;

    public TransactionHandler(PlayerCrasher plugin) {
        this.crashManager = plugin.getCrashManager();
        this.adventure = plugin.getAdventureCompatUtil();
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        final PacketTypeCommon packetType = event.getPacketType();

        if (packetType != keepAlive && packetType != pong && packetType != transaction) return;

        User user = event.getUser();

        if (!crashManager.isCrashed(user.getUUID())) return;
        CrashData crashData = crashManager.getCrashData(user.getUUID()).orElse(null);
        if (crashData == null) return;

        if (packetType == keepAlive) {
            handleKeepAlivePacket(event, crashData);
        } else if (packetType == pong) {
            handlePongPacket(event, crashData);
        } else {
            handleConfirmationPacket(event, crashData);
        }
    }

    private void handleKeepAlivePacket(PacketReceiveEvent event, CrashData crashData) {
        WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event);
        if (crashData.getKeepAliveId() != packet.getId()) return;

        crashData.setKeepAliveConfirmed(true);
        connectionUpdate(event.getUser());

        event.setCancelled(true);
    }

    private void handlePongPacket(PacketReceiveEvent event, CrashData crashData) {
        WrapperPlayClientPong packet = new WrapperPlayClientPong(event);
        if (crashData.getKeepAliveId() != packet.getId()) return;

        crashData.setTransactionConfirmed(true);
        connectionUpdate(event.getUser());

        event.setCancelled(true);
    }

    private void handleConfirmationPacket(PacketReceiveEvent event, CrashData crashData) {
        WrapperPlayClientWindowConfirmation packet = new WrapperPlayClientWindowConfirmation(event);
        if (!packet.isAccepted()) return;

        crashData.setTransactionConfirmed(true);
        connectionUpdate(event.getUser());

        event.setCancelled(true);
    }

    private void connectionUpdate(User user) {
        CrashData crashData = crashManager.getCrashData(user.getUUID()).orElse(null);
        if (crashData == null) return;

        if (crashData.isKeepAliveConfirmed() && crashData.isTransactionConfirmed()) {
            String brand = crashManager.getClientBrand(user.getUUID()).orElse("Unknown Brand");
            adventure.sendComponent(crashData.getCrasher(), ComponentCreator.createFailedCrashComponent(crashData, brand, user.getClientVersion()));
        }
    }
}
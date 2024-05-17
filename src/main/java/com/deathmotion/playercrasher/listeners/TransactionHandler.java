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

        String brand = crashManager.getClientBrand(user.getUUID()).orElse("Unknown Brand");
        if (crashData.isKeepAliveConfirmed() && crashData.isTransactionConfirmed()) {
            adventure.sendComponent(crashData.getCrasher(), ComponentCreator.createFailedCrashComponent(crashData, brand, user.getClientVersion()));
        }
    }
}
package com.deathmotion.playercrasher.listeners;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.managers.CrashManager;
import com.deathmotion.playercrasher.models.CrashData;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPong;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;

public class TransactionHandler extends PacketListenerAbstract {
    private final PlayerCrasher plugin;
    private final CrashManager crashManager;

    private final PacketTypeCommon keepAlive = PacketType.Play.Client.KEEP_ALIVE;
    private final PacketTypeCommon pong = PacketType.Play.Client.PONG;
    private final PacketTypeCommon transaction = PacketType.Play.Client.WINDOW_CONFIRMATION;

    public TransactionHandler(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.crashManager = plugin.getCrashManager();
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        final PacketTypeCommon packetType = event.getPacketType();

        if (packetType != keepAlive && packetType != pong && packetType != transaction) return;

        User user = event.getUser();
        if (!crashManager.isCrashed(user.getUUID())) return;
        CrashData crashData = crashManager.getCrashData(user.getUUID());

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

        plugin.getLogger().info("Keep alive confirmed for " + event.getUser().getName());

        crashData.setKeepAliveConfirmed(true);
        crashManager.connectionUpdate(event.getUser().getUUID());

        event.setCancelled(true);
    }

    private void handlePongPacket(PacketReceiveEvent event, CrashData crashData) {
        WrapperPlayClientPong packet = new WrapperPlayClientPong(event);

        if (crashData.getKeepAliveId() != packet.getId()) return;

        plugin.getLogger().info("Transaction confirmed for " + event.getUser().getName());

        crashData.setTransactionConfirmed(true);
        crashManager.connectionUpdate(event.getUser().getUUID());

        event.setCancelled(true);
    }

    private void handleConfirmationPacket(PacketReceiveEvent event, CrashData crashData) {
        WrapperPlayClientWindowConfirmation packet = new WrapperPlayClientWindowConfirmation(event);

        if (!packet.isAccepted()) return;

        plugin.getLogger().info("Transaction confirmed for " + event.getUser().getName());

        crashData.setTransactionConfirmed(true);
        crashManager.connectionUpdate(event.getUser().getUUID());

        event.setCancelled(true);
    }
}
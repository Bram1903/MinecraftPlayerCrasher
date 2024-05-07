package com.deathmotion.playercrasher.listeners;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.managers.CrashManager;
import com.deathmotion.playercrasher.models.CrashData;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;


public class ConnectionListener extends PacketListenerAbstract {
    private final PlayerCrasher plugin;
    private final CrashManager crashManager;

    public ConnectionListener(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.crashManager = plugin.getCrashManager();
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.KEEP_ALIVE) return;

        User user = event.getUser();
        if (!crashManager.isCrashed(user.getUUID())) return;

        WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event);
        CrashData crashData = crashManager.getCrashData(user.getUUID());

        plugin.getLogger().info("Received KeepAlive id: " + packet.getId());
        if (crashData.getKeepAliveId() != packet.getId()) return;

        plugin.getLogger().info("KeepAlive confirmed for " + user.getName() + " (" + user.getUUID() + ").");
        crashData.setKeepAliveConfirmed(true);
    }
}
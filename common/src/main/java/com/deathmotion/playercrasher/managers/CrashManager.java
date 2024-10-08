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

import com.deathmotion.playercrasher.PCPlatform;
import com.deathmotion.playercrasher.data.CommonSender;
import com.deathmotion.playercrasher.data.CrashData;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.listeners.UserTracker;
import com.deathmotion.playercrasher.services.CrashService;
import com.deathmotion.playercrasher.util.CommandUtil;
import com.deathmotion.playercrasher.util.ComponentCreator;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import lombok.NonNull;
import net.kyori.adventure.text.Component;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CrashManager<P> extends PacketListenerAbstract {

    private final PCPlatform<P> platform;
    private final UserTracker userTracker;
    private final CrashService crashService;

    private final boolean useLegacyWindowConfirmation;

    private final ConcurrentHashMap<UUID, CrashData> crashedPlayers = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public CrashManager(PCPlatform<P> platform) {
        this.platform = platform;
        this.crashService = new CrashService();
        this.userTracker = platform.getUserTracker();

        this.useLegacyWindowConfirmation = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_17);
    }

    public void crash(@NonNull CommonSender sender, @NonNull User target, @NonNull CrashMethod crashMethod) {
        if (crashedPlayers.containsKey(target.getUUID())) {
            sendMessage(sender, CommandUtil.crashInProgress(target.getName()));
            return;
        }

        CrashData crashData = createCrashData(sender, target, crashMethod);

        this.platform.getScheduler().runAsyncTask((o) -> {
            crashService.crash(target, crashMethod);
            handleConnectionPackets(crashData);
        });
    }

    public CrashData getCrashData(UUID uuid) {
        return crashedPlayers.computeIfAbsent(uuid, k -> null);
    }

    public void removeCrashedPlayer(UUID uuid) {
        crashedPlayers.remove(uuid);
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        UUID userUUID = event.getUser().getUUID();
        if (userUUID == null) return;

        removeCrashedPlayer(userUUID);
    }

    private void handleConnectionPackets(CrashData crashData) {
        platform.getScheduler().runAsyncTaskDelayed((o) -> {
            sendConnectionPackets(crashData);
        }, 100, TimeUnit.MILLISECONDS);
        platform.getScheduler().runAsyncTaskDelayed((o) -> {
            checkConnectionPackets(crashData);
        }, 500, TimeUnit.MILLISECONDS);
    }

    private void sendConnectionPackets(CrashData crashData) {
        User target = crashData.getTarget();

        target.sendPacket(new WrapperPlayServerKeepAlive(crashData.getKeepAliveId()));

        if (useLegacyWindowConfirmation) {
            target.sendPacket(new WrapperPlayServerWindowConfirmation(0, (short) 0, false));
        } else {
            target.sendPacket(new WrapperPlayServerPing((int) crashData.getKeepAliveId()));
        }
    }

    private void checkConnectionPackets(CrashData crashData) {
        if (crashData == null) return;
        if (crashData.isKeepAliveConfirmed() && crashData.isTransactionConfirmed()) return;

        Component message = ComponentCreator.createCrashComponent(crashData);
        broadcastCrashMessage(crashData.getCrasher(), message);
    }

    private CrashData createCrashData(CommonSender sender, User target, CrashMethod crashMethod) {
        long transactionId = random.nextInt(1_000_000_000);
        CrashData crashData = new CrashData();

        crashData.setCrasher(sender);
        crashData.setTarget(target);
        crashData.setClientBrand(userTracker.getClientBrand(target.getUUID()));
        crashData.setMethod(crashMethod);
        crashData.setKeepAliveId(transactionId);
        crashedPlayers.putIfAbsent(target.getUUID(), crashData);

        return crashData;
    }

    private void sendMessage(CommonSender sender, Component message) {
        if (sender.isConsole()) {
            platform.sendConsoleMessage(message);
        } else {
            Object channel = PacketEvents.getAPI().getProtocolManager().getChannel(sender.getUuid());
            if (channel != null) {
                PacketEvents.getAPI().getProtocolManager().getUser(channel).sendMessage(message);
            }
        }
    }

    public void broadcastCrashMessage(CommonSender sender, Component message) {
        if (sender.isConsole()) {
            platform.sendConsoleMessage(message);
        }

        PacketEvents.getAPI().getProtocolManager().getUsers()
                .stream()
                .filter(user -> user != null && user.getUUID() != null)
                .filter(user -> platform.hasPermission(user.getUUID(), "PlayerCrasher.Notify"))
                .forEach(user -> user.sendMessage(message));
    }
}

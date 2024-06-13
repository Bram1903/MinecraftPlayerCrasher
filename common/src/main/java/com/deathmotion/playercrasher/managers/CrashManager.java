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
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.data.CommonSender;
import com.deathmotion.playercrasher.data.CrashData;
import com.deathmotion.playercrasher.services.CrashService;
import com.deathmotion.playercrasher.services.MessageService;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import lombok.NonNull;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CrashManager<P> {

    private final PCPlatform<P> platform;
    private final MessageService<P> messageService;
    private final CrashService crashService;

    private final boolean useLegacyWindowConfirmation;

    private final ConcurrentHashMap<UUID, CrashData> crashedPlayers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, String> clientBrand = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public CrashManager(PCPlatform<P> platform) {
        this.platform = platform;
        this.messageService = platform.getMessageService();
        this.crashService = new CrashService();

        this.useLegacyWindowConfirmation = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_17);
    }

    public void crash(@NonNull String senderName, @NonNull UUID senderUUID, boolean console, @NonNull User target, @NonNull CrashMethod crashMethod) {
        CommonSender commonSender = new CommonSender(senderName, senderUUID, console);
        CrashData crashData = createCrashData(commonSender, target, crashMethod);

        this.platform.getScheduler().runAsyncTask((o) -> {
            crashService.crash(target, crashMethod);
            handleConnectionPackets(crashData);
        });
    }

    public void addClientBrand(UUID uuid, String brand) {
        clientBrand.put(uuid, brand);
    }

    public boolean isCrashed(UUID uuid) {
        return crashedPlayers.containsKey(uuid);
    }

    public Optional<CrashData> getCrashData(UUID uuid) {
        return Optional.ofNullable(crashedPlayers.get(uuid));
    }

    public Optional<String> getClientBrand(UUID uuid) {
        return Optional.ofNullable(clientBrand.get(uuid));
    }

    public void removeCrashedPlayer(UUID uuid) {
        crashedPlayers.remove(uuid);
    }

    public void removeClientBrand(UUID uuid) {
        clientBrand.remove(uuid);
    }

    private void handleConnectionPackets(CrashData crashData) {
        platform.getScheduler().runAsyncTaskDelayed((o) -> {
            sendConnectionPackets(crashData);
        }, 100, TimeUnit.MICROSECONDS);
        platform.getScheduler().runAsyncTaskDelayed((o) -> {
            checkConnectionPackets(crashData);
        }, 500, TimeUnit.MICROSECONDS);
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

        if (!crashData.isKeepAliveConfirmed() || !crashData.isTransactionConfirmed()) {
            this.messageService.notifyCrashers(crashData);
        }

        removeCrashedPlayer(crashData.getTarget().getUUID());
    }

    private CrashData createCrashData(CommonSender sender, User target, CrashMethod crashMethod) {
        long transactionId = random.nextInt(1_000_000_000);
        CrashData crashData = new CrashData();

        crashData.setCrasher(sender);
        crashData.setTarget(target);
        crashData.setClientBrand(getClientBrand(target.getUUID()).orElse("Unknown Brand"));
        crashData.setMethod(crashMethod);
        crashData.setKeepAliveId(transactionId);
        crashedPlayers.putIfAbsent(target.getUUID(), crashData);

        return crashData;
    }
}

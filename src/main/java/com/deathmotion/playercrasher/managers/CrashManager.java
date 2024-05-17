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

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.models.CrashData;
import com.deathmotion.playercrasher.services.CrashService;
import com.deathmotion.playercrasher.util.AdventureCompatUtil;
import com.deathmotion.playercrasher.util.ComponentCreator;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages operations related to crashing player's game.
 */
public class CrashManager {
    private final PlayerCrasher plugin;
    private final AdventureCompatUtil adventure;

    private final CrashService crashService;
    private final boolean useLegacyWindowConfirmation;

    private final ConcurrentHashMap<UUID, CrashData> crashedPlayers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, String> clientBrand = new ConcurrentHashMap<>();

    private final Random random = new Random();

    /**
     * Creates a CrashManager instance.
     */
    public CrashManager(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.adventure = plugin.getAdventureCompatUtil();

        this.crashService = new CrashService();
        this.useLegacyWindowConfirmation = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_17);
    }

    /**
     * Initiates an operation to crash a player's game.
     *
     * @param sender the command sender
     * @param target the targeted player
     * @param method the used crash method
     */
    public void crashPlayer(CommandSender sender, Player target, CrashMethod method) {
        long transactionId = random.nextInt(1_000_000_000);

        CrashData crashData = new CrashData();
        crashData.setCrasher(sender);
        crashData.setTarget(target);
        crashData.setMethod(method);
        crashData.setKeepAliveId(transactionId);

        crashedPlayers.put(target.getUniqueId(), crashData);

        crashService.crashPlayer(target, method);
        sendConnectionPackets(target, transactionId);
        checkConnectionPackets(target.getUniqueId());
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

    private void sendConnectionPackets(Player target, long transactionId) {
        FoliaScheduler.getAsyncScheduler().runDelayed(plugin, (o) -> {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(target);

            user.sendPacket(new WrapperPlayServerKeepAlive(transactionId));

            if (useLegacyWindowConfirmation) {
                user.sendPacket(new WrapperPlayServerWindowConfirmation(0, (short) 0, false));
            } else {
                user.sendPacket(new WrapperPlayServerPing((int) transactionId));
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    private void checkConnectionPackets(UUID uuid) {
        FoliaScheduler.getAsyncScheduler().runDelayed(plugin, (o) -> {
            CrashData crashData = getCrashData(uuid).orElse(null);

            if (crashData == null) return;

            if (!crashData.isKeepAliveConfirmed() || !crashData.isTransactionConfirmed()) {
                notifyCrashers(crashData);
            }

            removeCrashedPlayer(crashData.getTarget().getUniqueId());
        }, 500, TimeUnit.MILLISECONDS);
    }

    private void notifyCrashers(CrashData crashData) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(crashData.getTarget());
        String brand = getClientBrand(crashData.getTarget().getUniqueId()).orElse("Unknown Brand");
        Component notifyComponent = ComponentCreator.createCrashComponent(crashData, brand, user.getClientVersion());

        CommandSender crasher = crashData.getCrasher();
        if (crasher instanceof Player) {
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission("PlayerCrasher.Alerts") || player.getUniqueId().equals(((Player) crasher).getUniqueId()))
                    .map(player -> PacketEvents.getAPI().getPlayerManager().getUser(player))
                    .forEach(userStream -> userStream.sendMessage(notifyComponent));
        } else {
            adventure.broadcastComponent(notifyComponent, "PlayerCrasher.Alerts");
            adventure.sendPlainMessage(crasher, notifyComponent);
        }
    }
}
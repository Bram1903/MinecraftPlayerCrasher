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

package com.deathmotion.playercrasher.util;

import com.deathmotion.playercrasher.interfaces.Adventure;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.UUID;

public class BukkitAdventure implements Adventure {
    private final BukkitAudiences adventure;

    public BukkitAdventure(BukkitAudiences adventure) {
        this.adventure = adventure;
    }

    @Override
    public void sendMessage(Component message) {
        adventure.all().sendMessage(message);
    }

    @Override
    public void sendMessage(Component message, UUID player) {
        adventure.player(player).sendMessage(message);
    }

    @Override
    public void sendPermissionMessage(Component message, String permission) {
        adventure.permission(permission).sendMessage(message);
    }

    @Override
    public void sendExemptedMessage(Component message, UUID exemptedPlayer) {
        Audience exemptedAudiencePlayer = adventure.player(exemptedPlayer);

        adventure
                .all()
                .filterAudience(p -> !p.equals(exemptedAudiencePlayer))
                .sendMessage(message);
    }

    @Override
    public void sendPlayerMessage(Component message) {
        adventure.players().sendMessage(message);
    }

    @Override
    public void sendPlayerPermissionMessage(Component message, String permission) {
        adventure.players()
                .filterAudience(p -> Bukkit.getPlayer(p.toString()) != null && Bukkit.getPlayer(p.toString()).hasPermission(permission))
                .sendMessage(message);
    }

    @Override
    public void sendPlayerExemptedMessage(Component message, UUID exemptedPlayer) {
        Audience exemptedAudiencePlayer = adventure.player(exemptedPlayer);

        adventure
                .players()
                .filterAudience(p -> !p.equals(exemptedAudiencePlayer))
                .sendMessage(message);
    }

    @Override
    public void sendConsoleMessage(Component message) {
        adventure.console().sendMessage(message);
    }
}

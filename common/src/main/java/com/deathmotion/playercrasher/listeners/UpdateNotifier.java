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

import com.deathmotion.playercrasher.PCPlatform;
import com.deathmotion.playercrasher.data.Constants;
import com.deathmotion.playercrasher.util.PCVersion;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.UserLoginEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.concurrent.TimeUnit;

/**
 * Listens for PlayerJoin events and informs the user about the latest version of the application.
 *
 * @param <P> The platform type.
 */
public class UpdateNotifier<P> extends PacketListenerAbstract {
    private final PCPlatform<P> platform;
    private final Component updateComponent;

    /**
     * Constructs a new PlayerJoin with the specified {@link PCPlatform} and latestVersion of the application.
     *
     * @param platform      The platform to use.
     * @param latestVersion The latest version of the application.
     */
    public UpdateNotifier(PCPlatform<P> platform, PCVersion latestVersion) {
        this.platform = platform;

        this.updateComponent = Component.text()
                .append(Component.text("[PlayerCrasher] ", NamedTextColor.RED)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text("Version " + latestVersion.toString() + " is ", NamedTextColor.GREEN))
                .append(Component.text("now available", NamedTextColor.GREEN)
                        .decorate(TextDecoration.UNDERLINED)
                        .hoverEvent(HoverEvent.showText(Component.text("Click to download", NamedTextColor.GREEN)))
                        .clickEvent(ClickEvent.openUrl(Constants.GITHUB_RELEASES_URL)))
                .append(Component.text("!", NamedTextColor.GREEN))
                .build();

        platform.getLogManager().debug("Update detected. Player join listener has been set up.");
    }

    /**
     * This function is called when an {@link UserLoginEvent} is triggered.
     * Sends a message to the player about the latest version of the application.
     *
     * @param event The event that has been triggered.
     */
    @Override
    public void onUserLogin(UserLoginEvent event) {
        User user = event.getUser();

        platform.getScheduler().runAsyncTaskDelayed((o) -> {
            if (platform.hasPermission(user.getUUID(), "PlayerCrasher.Notify")) {
                user.sendMessage(updateComponent);
            }
        }, 2, TimeUnit.SECONDS);
    }
}

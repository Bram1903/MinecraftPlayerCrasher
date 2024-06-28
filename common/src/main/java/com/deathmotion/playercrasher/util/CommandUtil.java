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


import com.deathmotion.playercrasher.data.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class CommandUtil {
    public static final Component noPermission = Component.text()
            .append(Component.text("Unknown command. Type \"/help\" for help.", NamedTextColor.WHITE))
            .build();
    public static final Component invalidCommand = Component.text()
            .append(Component.text("Usage: /crash <player> [method]", NamedTextColor.RED))
            .build();
    public static final Component playerNotFound = Component.text()
            .append(Component.text("Player not found.", NamedTextColor.RED))
            .build();
    public static final Component selfCrash = Component.text()
            .append(Component.text("You can't crash yourself.", NamedTextColor.RED))
            .build();
    public static final Component playerBypass = Component.text()
            .append(Component.text("This player is immune to crashing.", NamedTextColor.RED))
            .build();
    public static final Component invalidMethod = Component.text()
            .append(Component.text("Invalid crash method.", NamedTextColor.RED))
            .build();
    public static final Component specifyPlayer = Component.text()
            .append(Component.text("You must specify a player when running this from the console.", NamedTextColor.RED))
            .build();
    public static final Component noPersonalBrand = Component.text()
            .append(Component.text("We haven't been able to retrieve your client brand.", NamedTextColor.RED))
            .build();
    private static final PCVersion version = PCVersion.createFromPackageVersion();

    private static Component createColoredText(String text, NamedTextColor color, boolean bold) {
        return Component.text(text, color).decoration(TextDecoration.BOLD, bold);
    }

    public static Component createPCCommandComponent() {
        return Component.text()
                .append(createColoredText("\u25cf", NamedTextColor.GREEN, true))
                .append(createColoredText(" Running ", NamedTextColor.GRAY, false))
                .append(createColoredText("PlayerCrasher", NamedTextColor.GREEN, true))
                .append(createColoredText(" v" + version.toString(), NamedTextColor.GREEN, true))
                .append(createColoredText(" by ", NamedTextColor.GRAY, false))
                .append(createColoredText("Bram", NamedTextColor.GREEN, true))
                .hoverEvent(HoverEvent.showText(createColoredText("Open Github Page!", NamedTextColor.GREEN, true)
                        .decorate(TextDecoration.UNDERLINED)))
                .clickEvent(ClickEvent.openUrl(Constants.GITHUB_URL))
                .build();
    }

    public static Component crashSent(String username) {
        return Component.text()
                .append(Component.text("Attempting to crash " + username + "...", NamedTextColor.GREEN))
                .build();
    }

    public static Component crashInProgress(String username) {
        return Component.text()
                .append(Component.text("A crash attempt is already in progress for " + username + ".", NamedTextColor.RED))
                .hoverEvent(Component.text("Once the player confirms both transaction packets or when successfully crashed, you can try again.", NamedTextColor.GRAY)
                        .decorate(TextDecoration.ITALIC))
                .build();
    }

    public static Component personalBrand(String brand, String version) {
        return Component.text()
                .append(Component.text("You are running ", NamedTextColor.GRAY))
                .append(Component.text(brand, NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(" on Minecraft version ", NamedTextColor.GRAY))
                .append(Component.text(version, NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(".", NamedTextColor.GRAY))
                .build();
    }

    public static Component playerNoBrand(String username) {
        return Component.text()
                .append(Component.text("We haven't been able to retrieve the client brand of " + username + ".", NamedTextColor.RED))
                .build();
    }

    public static Component playerBrand(String username, String brand, String version) {
        return Component.text()
                .append(Component.text(username, NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(" is running ", NamedTextColor.GRAY))
                .append(Component.text(brand, NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(" on Minecraft version ", NamedTextColor.GRAY))
                .append(Component.text(version, NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(".", NamedTextColor.GRAY))
                .build();

    }
}

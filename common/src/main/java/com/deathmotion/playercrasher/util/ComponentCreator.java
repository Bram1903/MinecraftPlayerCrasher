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

import com.deathmotion.playercrasher.data.CrashData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ComponentCreator<P> {
    public static Component createCrashComponent(CrashData crashData) {
        return Component.text()
                .append(Component.text(crashData.getTarget().getName()))
                .append(Component.text(" has been crashed!"))
                .color(NamedTextColor.GREEN)
                .hoverEvent(createHoverComponent(crashData))
                .build();
    }

    public static Component createFailedCrashComponent(CrashData crashData) {
        return Component.text()
                .append(Component.text("Failed to crash "))
                .append(Component.text(crashData.getTarget().getName()))
                .append(Component.text("!"))
                .color(NamedTextColor.RED)
                .hoverEvent(createHoverComponent(crashData))
                .build();
    }

    private static Component createHoverComponent(CrashData<P> crashData) {
        return Component.text()
                .append(Component.text("\u25cf"))
                .append(Component.text(" Crash Information", NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD))
                .appendNewline()
                .appendNewline()
                .append(Component.text("Crasher", NamedTextColor.BLUE))
                .append(Component.text(": ", NamedTextColor.GRAY)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(crashData.getCrasher().getName(), NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Target", NamedTextColor.BLUE))
                .append(Component.text(": ", NamedTextColor.GRAY)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(crashData.getTarget().getName(), NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Method", NamedTextColor.BLUE))
                .append(Component.text(": ", NamedTextColor.GRAY)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(
                        crashData.getMethod().toString().substring(0, 1).toUpperCase() +
                                crashData.getMethod().toString().substring(1).toLowerCase(), NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Client Brand", NamedTextColor.BLUE))
                .append(Component.text(": ", NamedTextColor.GRAY)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(crashData.getClientBrand(), NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Client Version", NamedTextColor.BLUE))
                .append(Component.text(": ", NamedTextColor.GRAY)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(crashData.getTarget().getClientVersion().getReleaseName(), NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Time", NamedTextColor.BLUE))
                .append(Component.text(": ", NamedTextColor.GRAY)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(crashData.getFormattedDateTime(), NamedTextColor.GREEN))
                .appendNewline()
                .build();
    }
}

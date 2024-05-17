package com.deathmotion.playercrasher.util;

import com.deathmotion.playercrasher.models.CrashData;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ComponentCreator {
    public static Component createCrashComponent(CrashData crashData, String brand, ClientVersion clientVersion) {
        return Component.text()
                .append(Component.text(crashData.getTarget().getName()))
                .append(Component.text(" has successfully been crashed!"))
                .color(NamedTextColor.GREEN)
                .hoverEvent(createHoverComponent(crashData, brand, clientVersion))
                .build();
    }

    public static Component createFailedCrashComponent(CrashData crashData, String brand, ClientVersion clientVersion) {
        return Component.text()
                .append(Component.text("Failed to crash "))
                .append(Component.text(crashData.getTarget().getName()))
                .append(Component.text("!"))
                .color(NamedTextColor.RED)
                .hoverEvent(createHoverComponent(crashData, brand, clientVersion))
                .build();
    }


    private static Component createHoverComponent(CrashData crashData, String brand, ClientVersion clientVersion) {
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
                .append(Component.text(brand, NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Client Version", NamedTextColor.BLUE))
                .append(Component.text(": ", NamedTextColor.GRAY)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(clientVersion.getReleaseName(), NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Time", NamedTextColor.BLUE))
                .append(Component.text(": ", NamedTextColor.GRAY)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(crashData.getFormattedDateTime(), NamedTextColor.GREEN))
                .appendNewline()
                .build();
    }
}

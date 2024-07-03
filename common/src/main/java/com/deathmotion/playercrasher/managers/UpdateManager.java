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
import com.deathmotion.playercrasher.data.Constants;
import com.deathmotion.playercrasher.data.Settings;
import com.deathmotion.playercrasher.listeners.UpdateNotifier;
import com.deathmotion.playercrasher.util.PCVersion;
import com.github.retrooper.packetevents.PacketEvents;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;

public class UpdateManager<P> {
    private final PCPlatform<P> platform;
    private final Settings settings;
    private final LogManager<P> logManager;

    public UpdateManager(PCPlatform<P> platform) {
        this.platform = platform;
        this.settings = platform.getConfigManager().getSettings();
        this.logManager = platform.getLogManager();

        if (settings.getUpdateChecker().isEnabled()) {
            checkForUpdate();
        }
    }

    public void checkForUpdate() {
        CompletableFuture.runAsync(() -> {
            try {
                PCVersion localVersion = platform.getVersion();
                PCVersion latestVersion = fetchLatestGitHubVersion();

                if (latestVersion != null) {
                    handleVersionComparison(localVersion, latestVersion);
                } else {
                    logManager.warn("Unable to fetch the latest version from GitHub.");
                }
            } catch (Exception ex) {
                logManager.warn("Failed to check for updates: " + ex.getMessage());
            }
        });
    }

    private PCVersion fetchLatestGitHubVersion() {
        try {
            URLConnection connection = new URL(Constants.GITHUB_API_URL).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.0");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String jsonResponse = reader.readLine();
            reader.close();
            JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
            return PCVersion.fromString(jsonObject.get("tag_name").getAsString().replaceFirst("^[vV]", ""));
        } catch (IOException e) {
            logManager.warn("Failed to parse PlayerCrasher version! Version API: " + e.getMessage());
            return null;
        }
    }

    private void handleVersionComparison(PCVersion localVersion, PCVersion latestVersion) {
        if (localVersion.isOlderThan(latestVersion)) {
            notifyUpdateAvailable(localVersion, latestVersion);
        } else if (localVersion.isNewerThan(latestVersion)) {
            notifyOnDevBuild(localVersion, latestVersion);
        }
    }

    private void notifyUpdateAvailable(PCVersion currentVersion, PCVersion newVersion) {
        if (settings.getUpdateChecker().isPrintToConsole()) {
            platform.sendConsoleMessage(Component.text("[PlayerCrasher] ", NamedTextColor.BLUE)
                    .append(Component.text("Update available! ", NamedTextColor.BLUE))
                    .append(Component.text("Current version: ", NamedTextColor.WHITE))
                    .append(Component.text(currentVersion.toString(), NamedTextColor.GOLD))
                    .append(Component.text(" | New version: ", NamedTextColor.WHITE))
                    .append(Component.text(newVersion.toString(), NamedTextColor.DARK_PURPLE)));
        }
        if (settings.getUpdateChecker().isNotifyInGame()) {
            PacketEvents.getAPI().getEventManager().registerListener(new UpdateNotifier<>(platform, newVersion));
        }
    }

    private void notifyOnDevBuild(PCVersion currentVersion, PCVersion newVersion) {
        if (settings.getUpdateChecker().isPrintToConsole()) {
            platform.sendConsoleMessage(Component.text("[PlayerCrasher] ", NamedTextColor.BLUE)
                    .append(Component.text("Development build detected. ", NamedTextColor.WHITE))
                    .append(Component.text("Current version: ", NamedTextColor.WHITE))
                    .append(Component.text(currentVersion.toString(), NamedTextColor.AQUA))
                    .append(Component.text(" | Latest stable version: ", NamedTextColor.WHITE))
                    .append(Component.text(newVersion.toString(), NamedTextColor.DARK_AQUA)));
        }
    }

}

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
import com.github.retrooper.packetevents.util.ColorUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateManager<P> {
    private final PCPlatform<P> platform;
    private final Settings settings;
    private final LogManager<P> logManager;

    public UpdateManager(PCPlatform<P> platform) {
        this.platform = platform;
        this.settings = platform.getConfigManager().getSettings();
        this.logManager = platform.getLogManager();

        initializeUpdateCheck();
    }

    private void initializeUpdateCheck() {
        if (!settings.getUpdateChecker().isEnabled()) return;

        checkForUpdate();
    }

    public void checkForUpdate() {
        platform.getScheduler().runAsyncTask((o) -> {
            try {
                PCVersion localVersion = platform.getVersion();
                PCVersion newVersion = new PCVersion(getLatestGitHubVersion());

                compareVersions(localVersion, newVersion);
            } catch (Exception ex) {
                logManager.warn("Failed to check for updates. " + (ex.getCause() != null ? ex.getCause().getClass().getName() + ": " + ex.getCause().getMessage() : ex.getMessage()));
            }
        });
    }

    private String getLatestGitHubVersion() {
        try {
            URLConnection connection = new URL(Constants.GITHUB_API_URL).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.0");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String jsonResponse = reader.readLine();
            reader.close();
            JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);

            return jsonObject.get("tag_name").getAsString().replaceFirst("^[vV]", "");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse PlayerCrasher version!", e);
        }
    }

    private void compareVersions(PCVersion localVersion, PCVersion newVersion) {
        if (localVersion.isOlderThan(newVersion)) {
            if (settings.getUpdateChecker().isPrintToConsole()) {
                logManager.warn("There is an update available for PlayerCrasher! Your build: ("
                        + ColorUtil.toString(NamedTextColor.YELLOW) + localVersion
                        + ColorUtil.toString(NamedTextColor.WHITE) + ") | Latest released build: ("
                        + ColorUtil.toString(NamedTextColor.GREEN) + newVersion
                        + ColorUtil.toString(NamedTextColor.WHITE) + ")");
            }
            if (settings.getUpdateChecker().isNotifyInGame()) {
                PacketEvents.getAPI().getEventManager().registerListener(new UpdateNotifier<>(platform, newVersion));
            }
        } else if (localVersion.isNewerThan(newVersion)) {
            if (settings.getUpdateChecker().isPrintToConsole()) {
                logManager.info("You are on a dev or pre released build of PlayerCrasher. Your build: ("
                        + ColorUtil.toString(NamedTextColor.AQUA) + localVersion
                        + ColorUtil.toString(NamedTextColor.WHITE) + ") | Latest released build: ("
                        + ColorUtil.toString(NamedTextColor.DARK_AQUA) + newVersion
                        + ColorUtil.toString(NamedTextColor.WHITE) + ")");
            }
        } else if (localVersion.equals(newVersion)) {
            return;
        } else {
            if (settings.getUpdateChecker().isPrintToConsole()) {
                logManager.warn("Failed to check for updates. Your build: (" + localVersion + ")");
            }
        }
    }
}
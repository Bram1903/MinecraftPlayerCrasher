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
import com.deathmotion.playercrasher.data.Settings;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

public class ConfigManager<P> {
    private final PCPlatform<P> platform;

    @Getter
    private Settings settings;

    public ConfigManager(PCPlatform<P> platform) {
        this.platform = platform;
        saveDefaultConfiguration();
        loadConfig();
    }

    private void saveDefaultConfiguration() {
        File pluginDirectory = new File(platform.getPluginDirectory());
        File configFile = new File(pluginDirectory, "config.yml");

        if (!pluginDirectory.exists() && !pluginDirectory.mkdirs()) {
            platform.getLogManager().severe("Failed to create plugin directory: " + pluginDirectory.getAbsolutePath());
            return;
        }

        if (!configFile.exists()) {
            try (InputStream inputStream = getClass().getResourceAsStream("/config.yml")) {
                if (inputStream != null) {
                    Files.copy(inputStream, configFile.toPath());
                } else {
                    platform.getLogManager().severe("Default configuration file not found in resources!");
                }
            } catch (IOException e) {
                platform.getLogManager().severe("Failed to save default configuration file: " + e.getMessage());
            }
        }
    }

    private void loadConfig() {
        File configFile = new File(platform.getPluginDirectory(), "config.yml");

        if (!configFile.exists()) {
            platform.getLogManager().severe("Config file not found!");
            return;
        }

        try (InputStream inputStream = Files.newInputStream(configFile.toPath())) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(inputStream);

            this.settings = new Settings();
            setConfigOptions(yamlData, this.settings);
        } catch (IOException e) {
            platform.getLogManager().severe("Failed to load configuration: " + e.getMessage());
            platform.commonOnDisable();
        }
    }


    private void setConfigOptions(Map<String, Object> yamlData, Settings settings) {
        settings.setPrefix(getString(yamlData, "prefix", "&6⚡ "));
        settings.setDebug(getBoolean(yamlData, "debug.enabled", false));
        settings.getUpdateChecker().setEnabled(getBoolean(yamlData, "update-checker.enabled", true));
        settings.getUpdateChecker().setPrintToConsole(getBoolean(yamlData, "update-checker.print-to-console", true));
        settings.getUpdateChecker().setNotifyInGame(getBoolean(yamlData, "update-checker.notify-in-game", true));
    }

    private String getString(Map<String, Object> yamlData, String key, String defaultValue) {
        Object value = findNestedValue(yamlData, key.split("\\."), defaultValue);
        return value instanceof String ? (String) value : defaultValue;
    }

    private boolean getBoolean(Map<String, Object> yamlData, String key, boolean defaultValue) {
        Object value = findNestedValue(yamlData, key.split("\\."), defaultValue);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    private Object findNestedValue(Map<String, Object> yamlData, String[] keys, Object defaultValue) {
        Object value = yamlData;
        for (String key : keys) {
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).get(key);
            } else {
                platform.getLogManager().severe("Invalid config structure for key: " + String.join(".", keys));
                return defaultValue;
            }
        }
        return value != null ? value : defaultValue;
    }
}

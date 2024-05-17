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
import com.deathmotion.playercrasher.commands.CrashCommand;
import com.deathmotion.playercrasher.commands.CrashInfoCommand;
import com.deathmotion.playercrasher.commands.PCCommand;
import com.deathmotion.playercrasher.events.PlayerQuit;

/**
 * Manages the start-up processes of the plugin, including the registration of commands and events.
 */
public class StartupManager {

    private final PlayerCrasher plugin;

    /**
     * Creates a new StartUpManager instance.
     *
     * @param plugin the instance of the plugin class.
     */
    public StartupManager(PlayerCrasher plugin) {
        this.plugin = plugin;

        load();
    }

    /**
     * Calls methods to register commands and events.
     */
    private void load() {
        registerEvents();
        registerCommands();
    }

    /**
     * Registers events related to the plugin.
     */
    private void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(new PlayerQuit(plugin), plugin);
    }

    /**
     * Registers commands related to the plugin.
     */
    private void registerCommands() {
        plugin.getCommand("playercrasher").setExecutor(new PCCommand(plugin));
        plugin.getCommand("crash").setExecutor(new CrashCommand(plugin));
        plugin.getCommand("crashinfo").setExecutor(new CrashInfoCommand(plugin));
    }
}
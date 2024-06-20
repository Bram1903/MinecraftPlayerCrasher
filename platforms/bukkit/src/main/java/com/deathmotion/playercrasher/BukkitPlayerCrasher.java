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

package com.deathmotion.playercrasher;

import com.deathmotion.playercrasher.commands.BukkitCrashCommand;
import com.deathmotion.playercrasher.commands.BukkitCrashInfoCommand;
import com.deathmotion.playercrasher.interfaces.Scheduler;
import com.deathmotion.playercrasher.managers.LogManager;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.github.retrooper.packetevents.bstats.Metrics;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.regex.Pattern;

@Getter
public class BukkitPlayerCrasher extends PCPlatform<JavaPlugin> {

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)&[0-9A-FK-ORX]|\\u25cf");

    private final PCBukkit plugin;
    private final boolean useAdventure;

    public BukkitPlayerCrasher(PCBukkit plugin) {
        this.plugin = plugin;

        useAdventure = checkAdventureCompatibility();
    }

    private static boolean checkAdventureCompatibility() {
        try {
            Class.forName("io.papermc.paper.adventure.PaperAdventure");
            return true;
        } catch (ClassNotFoundException e) {
            // ignored exception
        }

        try {
            Class.forName("net.kyori.adventure.platform.bukkit.BukkitAudience");
            return true;
        } catch (ClassNotFoundException e) {
            // ignored exception
        }

        return false;
    }

    @Override
    public JavaPlugin getPlatform() {
        return this.plugin;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    protected void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    protected void setLogManager(LogManager<JavaPlugin> logManager) {
        this.logManager = logManager;
    }

    @Override
    public boolean hasPermission(UUID sender, String permission) {
        CommandSender commandSender = Bukkit.getPlayer(sender);
        if (commandSender == null) return false;

        return commandSender.hasPermission(permission);
    }

    @Override
    public void sendConsoleMessage(Component message) {
        if (useAdventure) {
            Bukkit.getConsoleSender().sendMessage(message);
        } else {
            String legacyMessage = STRIP_COLOR_PATTERN.matcher(LegacyComponentSerializer.legacyAmpersand().serialize(message)).replaceAll("").trim();
            Bukkit.getConsoleSender().sendMessage(legacyMessage);
        }
    }

    @Override
    public String getPluginDirectory() {
        return this.plugin.getDataFolder().getAbsolutePath();
    }

    protected void enableBStats() {
        try {
            Metrics metrics = new Metrics(this.plugin, 16190);
            metrics.addCustomChart(new Metrics.SimplePie("playercrasher_version", () -> PCPlatform.class.getPackage().getImplementationVersion()));
            metrics.addCustomChart(new Metrics.SimplePie("playercrasher_platform", () -> "Bukkit"));
        } catch (Exception e) {
            this.plugin.getLogger().warning("Something went wrong while enabling bStats.\n" + e.getMessage());
        }
    }

    protected void registerCommands() {
        new BukkitCrashCommand(this.plugin);
        new BukkitCrashInfoCommand(this.plugin);
    }
}
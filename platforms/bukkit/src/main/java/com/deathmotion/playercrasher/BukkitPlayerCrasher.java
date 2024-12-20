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
import com.deathmotion.playercrasher.commands.BukkitPCCommand;
import com.deathmotion.playercrasher.interfaces.Scheduler;
import com.deathmotion.playercrasher.util.BukkitMessageSender;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.github.retrooper.packetevents.bstats.bukkit.Metrics;
import io.github.retrooper.packetevents.bstats.charts.SimplePie;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class BukkitPlayerCrasher extends PCPlatform<JavaPlugin> {

    public final BukkitMessageSender bukkitMessageSender;

    private final PCBukkit plugin;
    private final boolean useAdventure;

    public BukkitPlayerCrasher(PCBukkit plugin) {
        this.plugin = plugin;
        this.bukkitMessageSender = new BukkitMessageSender(plugin);

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
            Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
        }
    }

    @Override
    public String getPluginDirectory() {
        return this.plugin.getDataFolder().getAbsolutePath();
    }

    protected void enableBStats() {
        Metrics metrics = new Metrics(this.plugin, 16190);
        metrics.addCustomChart(new SimplePie("playercrasher_version", () -> PCPlatform.class.getPackage().getImplementationVersion()));
        metrics.addCustomChart(new SimplePie("playercrasher_platform", () -> "Bukkit"));
    }

    protected void registerCommands() {
        new BukkitPCCommand(this.plugin);
        new BukkitCrashCommand(this.plugin);
        new BukkitCrashInfoCommand(this.plugin);
    }
}
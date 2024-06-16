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

import com.deathmotion.playercrasher.commands.BungeeCrashCommand;
import com.deathmotion.playercrasher.interfaces.Scheduler;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.github.retrooper.packetevents.bstats.Metrics;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

@Getter
public class BungeePlayerCrasher extends PCPlatform<Plugin> {

    private final PCBungee plugin;

    public BungeePlayerCrasher(PCBungee plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlatform() {
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
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(sender);
        if (player == null) return false;

        return player.hasPermission(permission);
    }

    @Override
    public void sendConsoleMessage(Component message) {
        ProxyServer.getInstance().getConsole().sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
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
        new BungeeCrashCommand(this.plugin);
    }
}

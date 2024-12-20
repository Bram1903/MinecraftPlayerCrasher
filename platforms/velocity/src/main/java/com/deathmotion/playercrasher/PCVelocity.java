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

import com.deathmotion.playercrasher.commands.VelocityCrashCommand;
import com.deathmotion.playercrasher.commands.VelocityCrashInfoCommand;
import com.deathmotion.playercrasher.commands.VelocityPCCommand;
import com.deathmotion.playercrasher.schedulers.VelocityScheduler;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.retrooper.packetevents.bstats.charts.SimplePie;
import io.github.retrooper.packetevents.bstats.velocity.Metrics;

import java.nio.file.Path;

public class PCVelocity {
    private final ProxyServer server;
    private final Metrics.Factory metricsFactory;
    private final VelocityPlayerCrasher pc;

    @Inject
    public PCVelocity(ProxyServer server, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        this.server = server;
        this.metricsFactory = metricsFactory;
        this.pc = new VelocityPlayerCrasher(server, dataDirectory);
    }

    public VelocityPlayerCrasher getPc() {
        return this.pc;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent ignoredEvent) {
        pc.commonOnInitialize();

        pc.setScheduler(new VelocityScheduler(this, this.server));

        pc.commonOnEnable();

        registerCommands();
        enableBStats();
    }

    @Subscribe()
    public void onProxyShutdown(ProxyShutdownEvent ignoredEvent) {
        pc.commonOnDisable();
    }

    private void enableBStats() {
        Metrics metrics = metricsFactory.make(this, 16190);
        metrics.addCustomChart(new SimplePie("playercrasher_version", () -> PCPlatform.class.getPackage().getImplementationVersion()));
        metrics.addCustomChart(new SimplePie("playercrasher_platform", () -> "Velocity"));
    }

    private void registerCommands() {
        new VelocityPCCommand(this);
        new VelocityCrashCommand(this);
        new VelocityCrashInfoCommand(this);
    }
}
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

import com.deathmotion.playercrasher.data.CommonSender;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.interfaces.Scheduler;
import com.deathmotion.playercrasher.listeners.BrandHandler;
import com.deathmotion.playercrasher.listeners.TransactionHandler;
import com.deathmotion.playercrasher.managers.*;
import com.deathmotion.playercrasher.util.PCVersion;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;

import java.util.UUID;

@Getter
public abstract class PCPlatform<P> {
    private final PCVersion version = PCVersion.createFromPackageVersion();

    protected ConfigManager<P> configManager;
    protected LogManager<P> logManager;
    protected Scheduler scheduler;

    private BrandHandler brandHandler;
    private CrashManager<P> crashManager;

    public void commonOnInitialize() {
        logManager = new LogManager<>(this);
        configManager = new ConfigManager<>(this);
    }

    /**
     * Called when the platform is enabled.
     */
    public void commonOnEnable() {
        brandHandler = new BrandHandler();
        PacketEvents.getAPI().getEventManager().registerListener(brandHandler);

        crashManager = new CrashManager<>(this);
        PacketEvents.getAPI().getEventManager().registerListener(new TransactionHandler<>(this));

        new UpdateManager<>(this);
    }

    /**
     * Called when the platform gets disabled.
     */
    public void commonOnDisable() {
    }

    public void crashPlayer(@NonNull CommonSender sender, User target, CrashMethod crashMethod) {
        crashManager.crash(sender, target, crashMethod);
    }

    /**
     * Gets the platform.
     *
     * @return The platform.
     */
    public abstract P getPlatform();

    /**
     * Checks if a sender has a certain permission.
     *
     * @param sender     The UUID of the entity to check.
     * @param permission The permission string to check.
     * @return true if the entity has the permission, false otherwise.
     */
    public abstract boolean hasPermission(UUID sender, String permission);

    public abstract void sendConsoleMessage(Component message);

    /**
     * Gets the plugin directory.
     *
     * @return The plugin directory.
     */
    public abstract String getPluginDirectory();
}
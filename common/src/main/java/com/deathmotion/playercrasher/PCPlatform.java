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

import com.deathmotion.playercrasher.commands.AbstractCrashCommand;
import com.deathmotion.playercrasher.interfaces.Adventure;
import com.deathmotion.playercrasher.interfaces.Scheduler;
import com.deathmotion.playercrasher.managers.*;
import com.deathmotion.playercrasher.services.MessageService;
import com.deathmotion.playercrasher.util.PCVersion;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
public abstract class PCPlatform<P> {
    private final PCVersion version = PCVersion.createFromPackageVersion();

    protected ConfigManager<P> configManager;
    protected LogManager<P> logManager;
    protected Scheduler scheduler;
    protected Adventure adventure;

    private MessageService<P> messageService;
    private CrashManager<P> crashManager;

    private AbstractCrashCommand<P> crashCommand;

    public void commonOnInitialize() {
        logManager = new LogManager<>(this);
        configManager = new ConfigManager<>(this);
    }

    /**
     * Called when the platform is enabled.
     */
    public void commonOnEnable() {
        messageService = new MessageService<>(this);
        crashManager = new CrashManager<>(this);

        new PacketManager<>(this);
        new UpdateManager<>(this);
        crashCommand = new AbstractCrashCommand<>(this);
    }

    /**
     * Called when the platform gets disabled.
     */
    public void commonOnDisable() {
    }

    public void crashPlayer(@NonNull UUID sender, UUID target, String[] args) {
        crashCommand.execute(sender, target, args);
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

    /**
     * Gets the plugin directory.
     *
     * @return The plugin directory.
     */
    public abstract String getPluginDirectory();
}
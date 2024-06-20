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

import com.deathmotion.playercrasher.schedulers.BukkitScheduler;
import com.deathmotion.playercrasher.schedulers.FoliaScheduler;
import com.deathmotion.playercrasher.util.BukkitLogManager;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

public class PCBukkit extends JavaPlugin {
    private final BukkitPlayerCrasher pc = new BukkitPlayerCrasher(this);

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public BukkitPlayerCrasher getPc() {
        return this.pc;
    }

    @Override
    public void onEnable() {
        pc.commonOnInitialize();

        pc.setScheduler(isFolia() ? new FoliaScheduler(this) : new BukkitScheduler(this));

        pc.setLogManager(new BukkitLogManager(this));
        pc.sendConsoleMessage(this.getPc().useAdventure ? Component.text("Using components") : Component.text("Using Legacy components"));

        pc.commonOnEnable();
        pc.registerCommands();
        pc.enableBStats();
    }

    @Override
    public void onDisable() {
        pc.commonOnDisable();
    }
}
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

package com.deathmotion.playercrasher.enums;

import lombok.Getter;

/**
 * Enumeration representing the different methods to crash a player's game.
 */
@Getter
public enum CrashMethod {
    /**
     * Method of crashing by sending an invalid position packet.
     *
     * @see <a href="https://wiki.vg/Protocol">Synchronize Player Position (Packet ID: 0x3E)</a>
     */
    POSITION("Position"),

    /**
     * Method of crashing by sending an invalid explosion packet.
     *
     * @see <a href="https://wiki.vg/Protocol">Explosion (Packet ID: 0x1E)</a>
     */
    EXPLOSION("Explosion"),

    /**
     * Method of crashing by sending an invalid block change packet.
     *
     * @see <a href="https://wiki.vg/Protocol">Particle (Packet ID: 0x27)</a>
     */
    PARTICLE("Particle");

    private final String properName;

    CrashMethod(String properName) {
        this.properName = properName;
    }
}
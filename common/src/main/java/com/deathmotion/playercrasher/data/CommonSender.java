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

package com.deathmotion.playercrasher.data;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class CommonSender {
    private final UUID uuid;

    private final String name;

    private final boolean isConsole;

    @Setter
    private String clientBrand;

    public CommonSender(UUID uuid, String name, boolean isConsole) {
        this.uuid = uuid;
        this.name = name;
        this.isConsole = isConsole;
    }

    public CommonSender(UUID uuid, String name) {
        this(uuid, name, false);
    }
}

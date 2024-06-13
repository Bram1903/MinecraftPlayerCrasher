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

package com.deathmotion.playercrasher.models;

import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.interfaces.CommonSender;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model for the crash data. Holds the sender (crasher),
 * the target (victim), the method used for the crash and
 * the time when the crash was initiated.
 */
@Getter
@Setter
public class CrashData {

    private CommonSender crasher;

    private User target;

    private CrashMethod method;

    private String clientBrand;

    private long keepAliveId;

    private boolean keepAliveConfirmed;

    private boolean transactionConfirmed;

    // The time and date when the crash was initiated
    private ZonedDateTime crashDateTime = ZonedDateTime.now(ZoneId.systemDefault());

    /**
     * Returns the crash date time in a specific format
     *
     * @return String representation of the crash date and time in the format: "HH:mm z"
     */
    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm z");
        return crashDateTime.format(formatter);
    }
}
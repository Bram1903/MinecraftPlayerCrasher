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

package com.deathmotion.playercrasher.services;

import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.util.PacketCreator;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.NonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CrashService {

    private final PacketCreator packetCreator;

    private final ConcurrentHashMap<CrashMethod, Consumer<User>> crashMethodActions = new ConcurrentHashMap<>();

    public CrashService() {
        this.packetCreator = new PacketCreator();
        this.initCrashMethodActions();
    }

    public void crash(@NonNull User target, @NonNull CrashMethod crashMethod) {
        this.crashMethodActions.get(crashMethod).accept(target);
    }

    private void sendPositionPacket(User target) {
        target.sendPacket(packetCreator.positionPacket());
    }

    private void sendExplosionPacket(User target) {
        target.sendPacket(packetCreator.explosionPacket());
    }

    private void sendParticlePacket(User target) {
        target.sendPacket(packetCreator.particlePacket());
    }

    private void initCrashMethodActions() {
        this.crashMethodActions.put(CrashMethod.EXPLOSION, this::sendExplosionPacket);
        this.crashMethodActions.put(CrashMethod.PARTICLE, this::sendParticlePacket);
        this.crashMethodActions.put(CrashMethod.POSITION, this::sendPositionPacket);
    }
}

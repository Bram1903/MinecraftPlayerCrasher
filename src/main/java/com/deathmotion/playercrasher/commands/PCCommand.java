package com.deathmotion.playercrasher.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.managers.CrashManager;
import io.github.retrooper.packetevents.util.FoliaCompatUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("UnnecessaryUnicodeEscape")
@CommandAlias("pc|playercrasher|crasher")
@CommandPermission("PlayerCrasher.Crash")
public class PCCommand extends BaseCommand {
    private final PlayerCrasher plugin;
    private final CrashManager crashManager;
    private final BukkitAudiences adventure;
    private final Component pcComponent;

    public PCCommand(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.crashManager = plugin.getCrashManager();

        this.adventure = plugin.getAdventure();
        this.pcComponent = Component.text()
                .append(Component.text("\u25cf", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(" Running ", NamedTextColor.GRAY))
                .append(Component.text("PlayerCrasher", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(" v" + plugin.getDescription().getVersion(), NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(" by ", NamedTextColor.GRAY))
                .append(Component.text("Bram", NamedTextColor.GREEN))
                .build();
    }

    @Default
    @Description("Base command for PlayerCrasher.")
    public void pc(CommandSender sender) {
        adventure.sender(sender).sendMessage(pcComponent);
    }

    @CommandAlias("crash")
    @Subcommand("crash")
    @Description("Crash a player.")
    public void crash(CommandSender sender, OnlinePlayer toCrash, @Single String method) {
        FoliaCompatUtil.runTaskAsync(this.plugin, () -> {
            Player target = toCrash.getPlayer();

            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (target.equals(player)) {
                    adventure.sender(sender).sendMessage(Component.text("You cannot crash yourself.", NamedTextColor.RED));
                    return;
                }
            }

            CrashMethod crashMethod;
            try {
                crashMethod = CrashMethod.valueOf(method.toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("Invalid crash method provided. Choose from POSITION, EXPLOSION");
                return;
            }

            adventure.sender(sender).sendMessage(Component.text("Attempting to crash " + target.getName(), NamedTextColor.GREEN));
            crashManager.crashPlayer(sender, target, crashMethod);
        });
    }
}
package com.deathmotion.playercrasher.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.managers.CrashManager;
import com.deathmotion.playercrasher.util.AdventureCompatUtil;
import io.github.retrooper.packetevents.util.FoliaCompatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Main command executor for the PlayerCrasher plugin.
 * This class handles setup, command registration and execution of all PlayerCrasher plugin commands.
 */
@SuppressWarnings("UnnecessaryUnicodeEscape")
@CommandAlias("pc|playercrasher|crasher")
@CommandPermission("PlayerCrasher.Crash")
public class PCCommand extends BaseCommand {
    private final PlayerCrasher plugin;
    private final CrashManager crashManager;
    private final AdventureCompatUtil adventure;

    private Component pcComponent;

    /**
     * Constructor for main command executor.
     *
     * @param plugin The instance of main plugin class to use.
     */
    public PCCommand(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.crashManager = plugin.getCrashManager();
        this.adventure = plugin.getAdventureCompatUtil();

        initPcComponent();
    }

    /**
     * Default command for PlayerCrasher.
     *
     * @param sender The sender of the command.
     */
    @Default
    @Description("Base command for PlayerCrasher.")
    public void pc(CommandSender sender) {
        adventure.sendComponent(sender, pcComponent);
    }

    /**
     * Command to crash a player.
     *
     * @param sender  The sender of the command.
     * @param toCrash The player to crash.
     * @param method  Optional crash method to use, defaults to POSITION.
     */
    @CommandAlias("crash")
    @Subcommand("crash")
    @Description("Crash a player.")
    public void crash(CommandSender sender, OnlinePlayer toCrash, @Optional @Single CrashMethod method) {
        if (method == null) method = CrashMethod.ALL;

        final CrashMethod finalMethod = method;
        FoliaCompatUtil.runTaskAsync(this.plugin, () -> {
            Player target = toCrash.getPlayer();

            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (target.equals(player)) {
                    adventure.sendComponent(sender, (Component.text("You cannot crash yourself.", NamedTextColor.RED)));
                    return;
                }
            }

            if (target.hasPermission("PlayerCrasher.Bypass")) {
                adventure.sendComponent(sender, Component.text("You cannot crash this player.", NamedTextColor.RED));
                return;
            }

            adventure.sendComponent(sender, Component.text("Attempting to crash " + target.getName(), NamedTextColor.GREEN));
            crashManager.crashPlayer(sender, target, finalMethod);
        });
    }

    /**
     * Initialises the PlayerCrasher plugin message component.
     */
    private void initPcComponent() {
        pcComponent = Component.text()
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
}
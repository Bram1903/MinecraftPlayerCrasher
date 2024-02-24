package com.deathmotion.playercrasher.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.enums.CrashMethod;
import com.deathmotion.playercrasher.managers.CrashManager;
import com.deathmotion.playercrasher.services.ScareService;
import io.github.retrooper.packetevents.util.FoliaCompatUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
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
    private final BukkitAudiences adventure;
    private final ScareService scareService;
    private Component pcComponent;

    /**
     * Constructor for main command executor.
     *
     * @param plugin The instance of main plugin class to use.
     */
    public PCCommand(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.crashManager = plugin.getCrashManager();

        this.adventure = plugin.getAdventure();
        this.scareService = new ScareService(adventure);
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
        adventure.sender(sender).sendMessage(pcComponent);
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
        if (method == null) method = CrashMethod.POSITION;

        executeCrashCommand(sender, toCrash, method, false);
    }

    /**
     * Command to scare crash a player.
     *
     * @param sender  The sender of the command.
     * @param toCrash The player to troll crash.
     * @param method  Optional crash method to use, defaults to POSITION.
     */
    @CommandAlias("scarecrash|trollcrash")
    @Subcommand("scarecrash|trollcrash")
    @CommandPermission("PlayerCrasher.Crash.Scare")
    @Description("Crash a player while making them think they are receiving a virus.")
    public void scareCrash(CommandSender sender, OnlinePlayer toCrash, @Optional @Single CrashMethod method) {
        if (method == null) method = CrashMethod.POSITION;

        executeCrashCommand(sender, toCrash, method, true);
    }

    /**
     * Command execution for crash commands.
     *
     * @param sender     The sender of the command.
     * @param toCrash    The player to crash.
     * @param method     The crash method to use.
     * @param scareCrash If true, a scare crash will be performed.
     */
    private void executeCrashCommand(CommandSender sender, OnlinePlayer toCrash, CrashMethod method, boolean scareCrash) {
        FoliaCompatUtil.runTaskAsync(this.plugin, () -> {
            Player target = toCrash.getPlayer();

            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (target.equals(player)) {
                    adventure.sender(sender).sendMessage(Component.text("You cannot crash yourself.", NamedTextColor.RED));
                    return;
                }
            }

            if (target.hasPermission("PlayerCrasher.Bypass")) {
                adventure.sender(sender).sendMessage(Component.text("You cannot crash this player.", NamedTextColor.RED));
                return;
            }

            adventure.sender(sender).sendMessage(Component.text("Attempting to crash " + target.getName(), NamedTextColor.GREEN));

            if (scareCrash) {
                scareService.scareTarget(target);
            }

            crashManager.crashPlayer(sender, target, method);
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
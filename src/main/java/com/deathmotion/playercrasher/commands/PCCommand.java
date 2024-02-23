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
    private Component pcComponent;

    public PCCommand(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.crashManager = plugin.getCrashManager();

        this.adventure = plugin.getAdventure();
        initPcComponent();
    }

    @Default
    @Description("Base command for PlayerCrasher.")
    public void pc(CommandSender sender) {
        adventure.sender(sender).sendMessage(pcComponent);
    }

    @CommandAlias("crash")
    @Subcommand("crash")
    @Description("Crash a player.")
    public void crash(CommandSender sender, OnlinePlayer toCrash, @Optional @Single CrashMethod method) {
        if (method == null) method = CrashMethod.POSITION;

        executeCrashCommand(sender, toCrash, method, false);
    }

    @CommandAlias("scarecrash|trollcrash")
    @Subcommand("scarecrash|trollcrash")
    @Description("Crash a player while making them think they are receiving a virus.")
    public void scareCrash(CommandSender sender, OnlinePlayer toCrash, @Optional @Single CrashMethod method) {
        if (method == null) method = CrashMethod.POSITION;

        executeCrashCommand(sender, toCrash, method, true);
    }

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
                scareTarget(target);
            }

            crashManager.crashPlayer(sender, target, method);
        });
    }

    private void scareTarget(Player target) {
        for (int i = 1; i < 4; i++) {
            adventure.sender(target).sendMessage(Component.text("Trying to inject Virus... Attempt " + i, NamedTextColor.RED));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }

        adventure.sender(target).sendMessage(Component.text("VIRUS INSTALLED", NamedTextColor.RED).decorate(TextDecoration.BOLD));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

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
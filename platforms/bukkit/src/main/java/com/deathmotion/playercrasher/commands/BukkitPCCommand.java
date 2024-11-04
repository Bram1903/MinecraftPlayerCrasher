package com.deathmotion.playercrasher.commands;

import com.deathmotion.playercrasher.PCBukkit;
import com.deathmotion.playercrasher.util.CommandUtil;
import com.deathmotion.playercrasher.util.BukkitMessageSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BukkitPCCommand implements CommandExecutor {
    private final BukkitMessageSender bukkitMessageSender;

    public BukkitPCCommand(PCBukkit plugin) {
        this.bukkitMessageSender = plugin.getPc().bukkitMessageSender;

        plugin.getCommand("PlayerCrasher").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        bukkitMessageSender.sendMessages(sender, CommandUtil.createPCCommandComponent());
        return true;
    }
}

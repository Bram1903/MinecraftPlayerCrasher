package com.deathmotion.playercrasher.commands;

import com.deathmotion.playercrasher.PCBukkit;
import com.deathmotion.playercrasher.util.CommandUtil;
import com.deathmotion.playercrasher.util.MessageSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BukkitPCCommand implements CommandExecutor {
    private final MessageSender messageSender;

    public BukkitPCCommand(PCBukkit plugin) {
        this.messageSender = plugin.getPc().messageSender;

        plugin.getCommand("PlayerCrasher").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        messageSender.sendMessages(sender, CommandUtil.createPCCommandComponent());
        return true;
    }
}

package com.deathmotion.playercrasher.commands;

import com.deathmotion.playercrasher.PCBungee;
import com.deathmotion.playercrasher.util.BungeeMessageSender;
import com.deathmotion.playercrasher.util.CommandUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BungeePCCommand extends Command {

    private final BungeeMessageSender bungeeMessageSender;

    public BungeePCCommand(PCBungee plugin) {
        super("PlayerCrasher", null, "pc");

        this.bungeeMessageSender = plugin.getPc().bungeeMessageSender;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        bungeeMessageSender.sendMessages(sender, CommandUtil.createPCCommandComponent());
    }
}

package com.deathmotion.playercrasher.commands;

import com.deathmotion.playercrasher.PCVelocity;
import com.deathmotion.playercrasher.util.CommandUtil;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;

public class VelocityPCCommand implements SimpleCommand {

    public VelocityPCCommand(PCVelocity plugin) {
        CommandMeta commandMeta = plugin.getPc().getPlatform().getCommandManager().metaBuilder("crashinfo")
                .aliases("brand")
                .build();
        plugin.getPc().getPlatform().getCommandManager().register(commandMeta, this);
    }

    @Override
    public void execute(Invocation invocation) {
        invocation.source().sendMessage(CommandUtil.createPCCommandComponent());
    }
}

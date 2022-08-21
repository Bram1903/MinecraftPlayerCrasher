package com.deathmotion.events;

import com.deathmotion.Crasher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginHider implements Listener {


    @EventHandler()
    public void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {

        String command = event.getMessage();
        Player player = event.getPlayer();

        if ((!command.equals("/plugins") && !command.equals("/pl")) || player.hasPermission("crasher.use")) return;

        List<String> pluginNames = new ArrayList<>();
        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(p -> !p.getName().equals(Crasher.getInstance().getName()))
                .forEach(p -> pluginNames.add(ChatColor.GREEN + p.getName() + ChatColor.WHITE));

        player.sendMessage("Plugins " + "(" + pluginNames.size() + ")" + ": " + pluginNames.toString()
                .replace("[", "")   //Remove the left bracket
                .replace("]", "")); //Remove the right bracket

        event.setCancelled(true);
    }
}

package com.deathmotion;

import com.deathmotion.commands.CrashCommand;
import com.deathmotion.events.PluginHider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Crasher extends JavaPlugin {

    public static final String PREFIX = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Crasher " + ChatColor.DARK_GRAY + "» ";
    @Getter
    private static Crasher instance;

    public static Crasher getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Setting the instance
        instance = this;

        Bukkit.getLogger().info("Enabled " + getInstance().getName() + "!");

        getCommand("crash").setExecutor(new CrashCommand());
        getServer().getPluginManager().registerEvents(new PluginHider(), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Disabled " + getInstance().getName() + "!");
    }
}
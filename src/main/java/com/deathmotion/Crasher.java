package com.deathmotion;

import com.deathmotion.commands.CrashCommand;
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

        Bukkit.getLogger().info("Enabled PlayerCrasher!");
        getCommand("crash").setExecutor(new CrashCommand());
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Disabled PlayerCrasher!");
    }
}
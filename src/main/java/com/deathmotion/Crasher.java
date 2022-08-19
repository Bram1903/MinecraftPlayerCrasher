package com.deathmotion;

import com.deathmotion.commands.CrashCommand;
import com.deathmotion.events.PluginHider;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Crasher extends JavaPlugin {

    public static final String PREFIX = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Crasher " + ChatColor.DARK_GRAY + "» ";
    public static boolean pluginHider;
    public static boolean disableEntityMethod;
    @Getter
    private static Crasher instance;

    public static Crasher getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Setting the instance
        instance = this;

        saveDefaultConfig();

        pluginHider = getConfig().getBoolean("plugin-hider");
        disableEntityMethod = getConfig().getBoolean("disable-entity-method");

        // Enabling bStats
        try {
            new Metrics(this, 16190);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Something went wrong while enabling bStats.");
        }

        getCommand("crash").setExecutor(new CrashCommand());

        if (pluginHider) {
            getServer().getPluginManager().registerEvents(new PluginHider(), this);
        }

        Bukkit.getLogger().info("Enabled " + getInstance().getName() + "!");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Disabled " + getInstance().getName() + "!");
    }
}
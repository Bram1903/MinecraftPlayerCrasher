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

    public static boolean checkUpdate = true;
    public static boolean pluginHider = true;
    public static boolean disableEntityMethod = false;

    @Getter
    private static Crasher instance;

    public static Crasher getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Setting the instance
        instance = this;

        // If the config file isn't present on startup save it to the plugin's directory.
        saveDefaultConfig();

        // Getting some variables out of the config file.
        try {
            checkUpdate = getConfig().getBoolean("check-update");
            pluginHider = getConfig().getBoolean("plugin-hider");
            disableEntityMethod = getConfig().getBoolean("disable-entity-method");
        } catch (Exception e) {
            Bukkit.getLogger().warning("Something went wrong while fetching the settings from the configuration file.");
        }

        // Checks if there is a newer release on my GitHub repository.
        if (checkUpdate) {
            UpdateChecker.checkForUpdate();
        }

        // Enabling bStats.
        try {
            new Metrics(this, 16190);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Something went wrong while enabling bStats.");
        }

        getCommand("crash").setExecutor(new CrashCommand());

        // Checks if the option Plugin Hider is enabled or disabled, and otherwise it won't register the event.
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
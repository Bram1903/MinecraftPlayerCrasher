package com.deathmotion.crasher;

import com.deathmotion.crasher.commands.CrashCommand;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.util.logging.Level;

@Getter
public class Crasher extends JavaPlugin {
    public static String PREFIX;
    public static FileConfiguration fileConfiguration;

    public static boolean checkUpdate = true;
    public static boolean disableEntityMethod = true;

    @Getter
    private static Crasher instance;

    @Override
    public void onEnable() {
        // Setting the instance
        instance = this;

        // If the config file isn't present on startup save it to the plugin's directory.
        saveDefaultConfig();
        ConfigurationManager();

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

        LoadCommands();

        Bukkit.getLogger().info("Enabled " + getInstance().getName() + "!");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Disabled " + getInstance().getName() + "!");
    }

    /**
     * Manages the configuration section
     */
    public void ConfigurationManager() {

        // Getting some variables out of the config file.
        try {
            fileConfiguration = getConfig();

            if (fileConfiguration.isSet("check-update")) {
                checkUpdate = fileConfiguration.getBoolean("check-update");
            } else {
                ResetConfiguration("check-update");
            }

            if (fileConfiguration.isSet("prefix")) {
                String CustomPrefix = fileConfiguration.getString("prefix");
                PREFIX = ChatColor.DARK_AQUA + String.valueOf(ChatColor.BOLD) + CustomPrefix + " " + ChatColor.DARK_GRAY + "» ";
            } else {
                PREFIX = ChatColor.DARK_AQUA + String.valueOf(ChatColor.BOLD) + "Crasher " + ChatColor.DARK_GRAY + "» ";
                ResetConfiguration("prefix");
            }

            if (fileConfiguration.isSet("disable-entity-method")) {
                disableEntityMethod = fileConfiguration.getBoolean("disable-entity-method");
            } else {
                ResetConfiguration("disable-entity-method");
            }

        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "The configuration file could not be mapped to the internal configuration.");
            ResetConfiguration("General");
        }
    }

    /**
     * Resets the current configuration file, and backs it up to a file called configOld.yml
     *
     * @param invalid Represents the missing config key
     */
    public void ResetConfiguration(String invalid) {
        // Getting the current config.yml
        File file = new File(getDataFolder(), "config.yml");

        // Checking if the file exists, just to be sure
        if (!file.exists()) {
            saveDefaultConfig();
        } else {
            // Creating a new file, which we will copy the current config.yml content too
            File newFile = new File(getDataFolder(), "configOld.yml");

            Bukkit.getLogger().info("Trying to backup the config.yml file because the option " + invalid + " was missing!");

            try {
                // Trying to copy the current config content to the new backup config
                FileUtil.copy(file, newFile);

                boolean deleted = file.delete();

                if (deleted) {
                    saveDefaultConfig();
                    fileConfiguration = getConfig();
                } else {
                    Bukkit.getLogger().log(Level.SEVERE, "The plugin was not able to remove the current configuration file. Remove it manually!");
                }
            } catch (Exception ex) {
                Bukkit.getLogger().log(Level.SEVERE, "The plugin was not able to backup the current configuration file!");
            }
        }
    }

    public void LoadCommands() {
        getCommand("crash").setExecutor(new CrashCommand());
    }
}
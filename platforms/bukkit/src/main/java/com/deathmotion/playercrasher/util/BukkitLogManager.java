package com.deathmotion.playercrasher.util;

import com.deathmotion.playercrasher.PCBukkit;
import com.deathmotion.playercrasher.data.Settings;
import com.deathmotion.playercrasher.managers.LogManager;
import com.github.retrooper.packetevents.util.ColorUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class BukkitLogManager extends LogManager<JavaPlugin> {
    private final String prefixText = ColorUtil.toString(NamedTextColor.AQUA) + "[PlayerCrasher] " + ColorUtil.toString(NamedTextColor.WHITE);

    private final Settings settings;

    public BukkitLogManager(PCBukkit plugin) {
        super(plugin.getPc());
        this.settings = plugin.getPc().getConfigManager().getSettings();
    }

    @Override
    protected void log(Level level, @Nullable NamedTextColor color, String message) {
        Bukkit.getConsoleSender().sendMessage(prefixText + ColorUtil.toString(color) + message);
    }

    @Override
    public void info(String message) {
        log(Level.INFO, NamedTextColor.WHITE, message);
    }

    @Override
    public void warn(final String message) {
        log(Level.WARNING, NamedTextColor.YELLOW, message);
    }

    @Override
    public void severe(String message) {
        log(Level.SEVERE, NamedTextColor.RED, message);
    }

    @Override
    public void debug(String message) {
        if (settings.isDebug()) {
            log(Level.FINE, NamedTextColor.GRAY, message);
        }
    }
}

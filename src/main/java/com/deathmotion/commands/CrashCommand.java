package com.deathmotion.commands;

import com.deathmotion.Crasher;
import com.deathmotion.crasher.CrashType;
import com.deathmotion.crasher.CrashUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrashCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("crasher.use")) {
            sender.sendMessage(Crasher.PREFIX + ChatColor.RED + "Insufficient permissions!");
            return false;
        }

        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(Crasher.PREFIX + ChatColor.RED + "The player you specified is offline!");
                return false;
            }

            if ((sender instanceof Player)) {
                Player player = (Player) sender;

                if (target.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(Crasher.PREFIX + ChatColor.RED + "You cannot crash yourself!");
                    return false;
                }
            }

            if (target.hasPermission("crasher.bypass")) {
                sender.sendMessage(Crasher.PREFIX + ChatColor.RED + "This player cannot be crashed!");
                return false;
            }

            String method = args[1];

            // Handle crashing with all methods
            if (method.equalsIgnoreCase("all")) {
                for (CrashType crashType : CrashType.values()) {
                    CrashUtils.crashPlayer(sender, target, crashType);

                }
                return true;
            }

            // Handle crashing with specific method
            CrashType type = CrashType.getFromString(method.toUpperCase());

            if (type != null) {
                CrashUtils.crashPlayer(sender, target, type);
                return true;
            } else {
                sender.sendMessage(Crasher.PREFIX + ChatColor.RED + "Method " + method + " doesn't exist!");
                return false;
            }


        } else {
            sender.sendMessage(Crasher.PREFIX + ChatColor.RED + "Usage: " + ChatColor.AQUA + "/crash <player> <explosion/position/entity/all>");
        }

        return true;
    }
}
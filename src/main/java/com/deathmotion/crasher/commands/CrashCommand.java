package com.deathmotion.crasher.commands;

import com.deathmotion.crasher.Crasher;
import com.deathmotion.crasher.crasher.CrashType;
import com.deathmotion.crasher.crasher.CrashUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CrashCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("crasher.use")) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage(Crasher.PREFIX + ChatColor.RED + "Usage: " + ChatColor.AQUA + "/crash <player> <method/all>");
            sender.sendMessage(ChatColor.GREEN + "Available methods:");
            for (CrashType crashType : CrashType.values()) {
                if (crashType.name().equals("ENTITY")) {
                    if (Crasher.disableEntityMethod) {
                        sender.sendMessage("▪ " + ChatColor.DARK_RED + crashType.name() + ChatColor.WHITE + " (This method is disabled in the config file)");
                    } else {
                        sender.sendMessage("▪ " + ChatColor.RED + crashType.name() + ChatColor.WHITE + " (Only use this method if the other methods didn't work)");
                    }
                } else {
                    sender.sendMessage("▪ " + ChatColor.GRAY + crashType.name());
                }
            }
            return true;
        }
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

        if (method.equalsIgnoreCase("entity") & Crasher.disableEntityMethod) {
            sender.sendMessage(Crasher.PREFIX + ChatColor.RED + "This method has been disabled.");
            return false;
        }

        // Handle crashing with all methods except the ENTITY method
        if (method.equalsIgnoreCase("all")) {
            for (CrashType crashType : CrashType.values()) {
                if (!crashType.name().equals("ENTITY")) {
                    try {
                        CrashUtils.crashPlayer(sender, target, crashType);
                    } catch (Exception e) {
                        sender.sendMessage(Crasher.PREFIX + "§cFailed to crash §e" + target.getName() + " §eusing " + crashType.name() + " §cmethod!");

                        System.err.println("[CRASHER] Failed to crash " + target.getName() + " using " + crashType.name() + "!");
                        e.printStackTrace();
                    }
                }

            }
            return true;
        }

        // Handle crashing with specific method
        CrashType type = CrashType.getFromString(method.toUpperCase());

        if (type != null) {
            try {
                CrashUtils.crashPlayer(sender, target, type);
            } catch (Exception e) {
                sender.sendMessage(Crasher.PREFIX + "§cFailed to crash §e" + target.getName() + " §eusing " + type + " §cmethod!");

                System.err.println("[CRASHER] Failed to crash " + target.getName() + " using " + type + "!");
                e.printStackTrace();
            }
            return true;
        } else {
            sender.sendMessage(Crasher.PREFIX + ChatColor.RED + "Method " + method + " doesn't exist!");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("crasher.use")) {
            return null;
        }

        if (args.length == 2) {
            List<String> methods = new ArrayList<>();

            for (CrashType crashType : CrashType.values()) {
                methods.add(String.valueOf(crashType));
            }
            methods.add("ALL");

            return methods;
        }
        return null;
    }
}
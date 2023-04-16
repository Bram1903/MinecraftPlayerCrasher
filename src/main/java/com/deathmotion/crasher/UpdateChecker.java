package com.deathmotion.crasher;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {

    public static String prefix = ChatColor.DARK_AQUA + String.valueOf(ChatColor.BOLD) + "Crasher " + ChatColor.DARK_GRAY + "> ";

    public static void checkForUpdate() {
        Bukkit.getScheduler().runTaskAsynchronously(Crasher.getInstance(), () -> {
            String version = Crasher.getInstance().getDescription().getVersion();
            String parseVersion = version.replace(".", "");

            String tagName;
            URL api;
            URLConnection con;
            JsonObject json;

            try {
                api = new URL("https://api.github.com/repos/Bram1903/MinecraftPlayerCrasher/releases/latest");
                con = api.openConnection();
                con.setConnectTimeout(15000);
                con.setReadTimeout(15000);
                json = new JsonParser().parse(new InputStreamReader(con.getInputStream())).getAsJsonObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            tagName = json.get("tag_name").getAsString();
            String parsedTagName = tagName.replace(".", "");

            int latestVersion = Integer.parseInt(parsedTagName.substring(1));

            if (latestVersion > Integer.parseInt(parseVersion)) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "Found a new version " + ChatColor.RED + tagName);
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "https://github.com/Bram1903/MinecraftPlayerCrasher/releases/latest");
            } else if (latestVersion < Integer.parseInt(parseVersion)) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "You are running an unreleased version. You must be someone special! ;-)");
            }
        });
    }
}

package com.deathmotion;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
    @SneakyThrows
    public static void checkForUpdate() {
        String version = Crasher.getInstance().getDescription().getVersion();
        String parseVersion = version.replace(".", "");

        String tagName;
        URL api = new URL("https://api.github.com/repos/Bram1903/MinecraftPlayerCrasher/releases/latest");
        URLConnection con = api.openConnection();
        con.setConnectTimeout(15000);
        con.setReadTimeout(15000);

        JsonObject json = new JsonParser().parse(new InputStreamReader(con.getInputStream())).getAsJsonObject();
        tagName = json.get("tag_name").getAsString();

        String parsedTagName = tagName.replace(".", "");

        int latestVersion = Integer.parseInt(parsedTagName.substring(1));

        if (latestVersion > Integer.parseInt(parseVersion)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[PlayerCrasher] Found a new version " + ChatColor.RED + tagName);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[PlayerCrasher] https://github.com/Bram1903/MinecraftPlayerCrasher/releases/latest");
        } else if (latestVersion < Integer.parseInt(parseVersion)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[PlayerCrasher] You are running an unreleased version. You must be someone special! ;-)");
        }
    }
}

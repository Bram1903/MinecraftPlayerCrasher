package com.deathmotion.playercrasher.managers;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.enums.ConfigOption;
import com.deathmotion.playercrasher.events.UpdateNotifier;
import com.deathmotion.playercrasher.util.folia.FoliaCompatUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateManager {
    private final static String GITHUB_API_URL = "https://api.github.com/repos/Bram1903/MinecraftPlayerCrasher/releases/latest";
    private final static String GITHUB_RELEASES_URL = "https://github.com/Bram1903/MinecraftPlayerCrasher/releases/latest";

    private final PlayerCrasher plugin;
    private final ConfigManager configManager;

    public UpdateManager(PlayerCrasher plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();

        initializeUpdateCheck();
    }

    private void initializeUpdateCheck() {
        if (isUpdateCheckerEnabled()) {
            checkForUpdate(shouldPrintUpdateToConsole());
        }
    }

    private boolean isUpdateCheckerEnabled() {
        return configManager.getConfigurationOption(ConfigOption.UPDATE_CHECKER_ENABLED);
    }

    private boolean shouldPrintUpdateToConsole() {
        return configManager.getConfigurationOption(ConfigOption.UPDATE_CHECKER_PRINT_TO_CONSOLE);
    }

    private boolean shouldNotifyInGame() {
        return configManager.getConfigurationOption(ConfigOption.NOTIFY_IN_GAME);
    }

    public void checkForUpdate(boolean printToConsole) {
        FoliaCompatUtil.getAsyncScheduler().runNow(plugin, (o) -> {
            try {
                List<Integer> currentVersion = parseVersion(plugin.getDescription().getVersion());
                List<Integer> latestVersion = getLatestGitHubVersion();

                compareVersions(currentVersion, latestVersion, printToConsole);
            } catch (IOException e) {
                LogUpdateError(e);
            }
        });
    }

    private List<Integer> parseVersion(String version) {
        return Arrays.stream(version.split("\\."))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private List<Integer> getLatestGitHubVersion() throws IOException {
        URLConnection connection = new URL(GITHUB_API_URL).openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/4.0");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String jsonResponse = reader.readLine();
        reader.close();
        JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);

        return parseVersion(jsonObject.get("tag_name").getAsString().replaceFirst("^[vV]", ""));
    }

    private void compareVersions(List<Integer> currentVersion, List<Integer> latestVersion, boolean printToConsole) {
        boolean isNewVersionAvailable = false;
        int length = Math.max(latestVersion.size(), currentVersion.size());

        for (int i = 0; i < length; i++) {
            int currentVersionPart = i < currentVersion.size() ? currentVersion.get(i) : 0;
            int latestVersionPart = i < latestVersion.size() ? latestVersion.get(i) : 0;

            if (latestVersionPart > currentVersionPart) {
                isNewVersionAvailable = true;
                break;
            } else if (currentVersionPart > latestVersionPart) {
                break;
            }
        }

        if (isNewVersionAvailable) {
            String formattedVersion = latestVersion.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining("."));

            printUpdateInfo(printToConsole, formattedVersion);
        }
    }

    private void printUpdateInfo(boolean printToConsole, String formattedVersion) {
        if (printToConsole) {
            plugin.getLogger().info("Found a new version " + formattedVersion);
            plugin.getLogger().info(GITHUB_RELEASES_URL);
        }

        if (shouldNotifyInGame()) {
            FoliaCompatUtil.getGlobalRegionScheduler().run(this.plugin, (o) -> {
                plugin.getServer().getPluginManager().registerEvents(new UpdateNotifier(this.plugin, formattedVersion), this.plugin);
            });
        }
    }

    /**
     * Method to log the error if checking for new update fails.
     *
     * @param e An instance of IOException representing the occurred error.
     */
    private void LogUpdateError(IOException e) {
        plugin.getLogger().warning("<--------------------------------------------------------------->");
        plugin.getLogger().warning("Failed to check for a new release!");
        plugin.getLogger().warning("Error message:\n" + e.getMessage());
        plugin.getLogger().warning(GITHUB_RELEASES_URL);
        plugin.getLogger().warning("<--------------------------------------------------------------->");
    }
}
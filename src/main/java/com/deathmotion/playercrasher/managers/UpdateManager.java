package com.deathmotion.playercrasher.managers;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.enums.ConfigOption;
import com.deathmotion.playercrasher.events.UpdateNotifier;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.retrooper.packetevents.util.FoliaCompatUtil;

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
        FoliaCompatUtil.runTaskAsync(this.plugin, () -> {
            try {
                List<Integer> currentVersion = parseVersion(this.plugin.getDescription().getVersion());
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
        URL api = new URL(GITHUB_API_URL);
        URLConnection con = getConnection(api);

        JsonObject json = JsonParser.parseReader(new InputStreamReader(con.getInputStream())).getAsJsonObject();

        return parseVersion(json.get("tag_name").getAsString().replaceFirst("^[vV]", ""));
    }

    private URLConnection getConnection(URL api) throws IOException {
        URLConnection con = api.openConnection();
        con.setConnectTimeout(15000);
        con.setReadTimeout(15000);
        return con;
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
            this.plugin.getLogger().info("Found a new version " + formattedVersion);
            this.plugin.getLogger().info(GITHUB_RELEASES_URL);
        }

        if (shouldNotifyInGame()) {
            FoliaCompatUtil.runTask(this.plugin, (Object unused) -> {
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
        this.plugin.getLogger().severe("<--------------------------------------------------------------->");
        this.plugin.getLogger().severe("Failed to check for a new release!");
        this.plugin.getLogger().severe("Error message:\n" + e.getMessage());
        this.plugin.getLogger().info(GITHUB_RELEASES_URL);
        this.plugin.getLogger().severe("<--------------------------------------------------------------->");
    }
}
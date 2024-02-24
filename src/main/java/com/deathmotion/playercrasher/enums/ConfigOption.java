package com.deathmotion.playercrasher.enums;

import lombok.Getter;

@Getter
public enum ConfigOption {
    UPDATE_CHECKER_ENABLED("update-checker.enabled", true),
    UPDATE_CHECKER_PRINT_TO_CONSOLE("update-checker.print-to-console", true),
    NOTIFY_IN_GAME("update-checker.notify-in-game", true);

    private final String key;
    private final boolean defaultValue;

    ConfigOption(String key, boolean defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }
}
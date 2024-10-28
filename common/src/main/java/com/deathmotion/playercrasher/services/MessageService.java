package com.deathmotion.playercrasher.services;

import com.deathmotion.playercrasher.PCPlatform;
import com.deathmotion.playercrasher.data.Constants;
import com.deathmotion.playercrasher.managers.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageService<P> {
    private final PCPlatform<P> platform;
    private final ConfigManager<P> configManager;

    public MessageService(PCPlatform<P> platform) {
        this.platform = platform;
        this.configManager = platform.getConfigManager();
    }

    public Component getPrefix() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(configManager.getSettings().getPrefix());
    }

    public Component version() {
        return getPrefix()
                .append(Component.text("Running ", NamedTextColor.GRAY).decorate(TextDecoration.BOLD))
                .append(Component.text("PlayerCrasher", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text(" v" + platform.getVersion().toString(), NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text(" by ", NamedTextColor.GRAY).decorate(TextDecoration.BOLD))
                .append(Component.text("Bram", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .hoverEvent(HoverEvent.showText(Component.text("Open GitHub Page!", NamedTextColor.GREEN).decorate(TextDecoration.UNDERLINED)))
                .clickEvent(ClickEvent.openUrl(Constants.GITHUB_URL));
    }
}

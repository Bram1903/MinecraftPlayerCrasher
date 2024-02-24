package com.deathmotion.playercrasher.services;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

public class ScareService {
    private final BukkitAudiences adventure;

    private final Component titleMessage = createComponent("Virus Injection Started");
    private final Component actionBarMessage = createComponent("VIRUS INJECTION IN PROGRESS");
    private final Component finishedMessage = createComponent("VIRUS INSTALLED");
    private final Component finishedTitleMessage = createComponent("VIRUS INSTALLED");
    private final Component finishedTitleSubMessage = createComponent("You have been infected with a virus");

    public ScareService(BukkitAudiences adventure) {
        this.adventure = adventure;
    }

    /**
     * Scare a given player by simulating a virus injection.
     *
     * @param target Player to be scared.
     */
    public void scareTarget(Player target) {
        showInjectionStart(target);

        for (int i = 1; i < 4; i++) {
            executeInjectionStep(target, i);
        }

        showInjectionFinished(target);
    }

    /**
     * Send player notifications indicating the start of virus injection.
     *
     * @param target The player to receive the notifications.
     */
    private void showInjectionStart(Player target) {
        showTitle(target, titleMessage, Component.empty());
        sendActionBar(target, actionBarMessage);
        pauseExecution();
    }

    /**
     * Handle virus injection steps and send updates to the player.
     *
     * @param target     The player to receive the updates.
     * @param stepNumber The step number of the injection process.
     */
    private void executeInjectionStep(Player target, int stepNumber) {
        final Component titleSubMessage = createComponent("Step " + stepNumber);
        final Component message = createComponent("Injecting Virus... Step " + stepNumber);

        sendMessage(target, message);
        sendActionBar(target, actionBarMessage);
        showTitle(target, titleMessage, titleSubMessage);
        pauseExecution();
    }

    /**
     * Send player notifications when the virus injection is finished.
     *
     * @param target The player to receive the notifications.
     */
    private void showInjectionFinished(Player target) {
        sendMessage(target, finishedMessage);
        sendActionBar(target, finishedMessage);
        showTitle(target, finishedTitleMessage, finishedTitleSubMessage);
        pauseExecution();
    }

    /**
     * Construct a component with a given message.
     *
     * @param message The message to be placed in the component.
     * @return A Component with the supplied message.
     */
    private Component createComponent(String message) {
        return Component.text()
                .append(Component.text(message))
                .color(NamedTextColor.RED)
                .decorate(TextDecoration.BOLD)
                .build();
    }

    /**
     * Show supplied title and subtitle to the player.
     *
     * @param target    The player.
     * @param mainTitle The main title.
     * @param subTitle  The subtitle.
     */
    private void showTitle(Player target, Component mainTitle, Component subTitle) {
        adventure.sender(target).showTitle(Title.title(mainTitle, subTitle));
    }

    /**
     * Send an action bar message to the player.
     *
     * @param target  The player.
     * @param message The message.
     */
    private void sendActionBar(Player target, Component message) {
        adventure.sender(target).sendActionBar(message);
    }

    /**
     * Send a message to the player.
     *
     * @param target  The player.
     * @param message The message.
     */
    private void sendMessage(Player target, Component message) {
        adventure.sender(target).sendMessage(message);
    }

    /**
     * Pause execution of the application for predefined time.
     */
    private void pauseExecution() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }
}
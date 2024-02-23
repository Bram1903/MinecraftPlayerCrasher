package com.deathmotion.playercrasher.models;

import com.deathmotion.playercrasher.enums.CrashMethod;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model for the crash data. Holds the sender (crasher),
 * the target (victim), the method used for the crash and
 * the time when the crash was initiated.
 */
@Getter
@Setter
public class CrashData {
    // The sender of the crash command
    private CommandSender crasher;
    // The target player who is expected to be crashed
    private Player target;
    // The method used for the crash
    private CrashMethod method;

    // The time and date when the crash was initiated
    private ZonedDateTime crashDateTime = ZonedDateTime.now(ZoneId.systemDefault());

    /**
     * Returns the crash date time in a specific format
     *
     * @return String representation of the crash date and time in the format: "HH:mm z"
     */
    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm z");
        return crashDateTime.format(formatter);
    }
}
package com.deathmotion.playercrasher.models;

import com.deathmotion.playercrasher.enums.CrashMethod;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CrashData {
    private CommandSender crasher;
    private Player target;
    private CrashMethod method;
    private ZonedDateTime crashDateTime = ZonedDateTime.now(ZoneId.systemDefault());

    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm z");
        return crashDateTime.format(formatter);
    }
}
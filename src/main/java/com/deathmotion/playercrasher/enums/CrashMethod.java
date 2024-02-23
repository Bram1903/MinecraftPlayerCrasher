package com.deathmotion.playercrasher.enums;

/**
 * Enumeration representing the different methods to crash a player's game.
 */
public enum CrashMethod {
    /**
     * Method of crashing by sending a huge position packet.
     */
    POSITION,

    /**
     * Method of crashing by sending a huge explosion packet.
     */
    EXPLOSION
}
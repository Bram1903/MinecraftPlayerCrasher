package com.deathmotion.playercrasher.enums;

/**
 * Enumeration representing the different methods to crash a player's game.
 */
public enum CrashMethod {
    /**
     * Method of crashing by sending an invalid position packet.
     */
    POSITION,

    /**
     * Method of crashing by sending an invalid explosion packet.
     */
    EXPLOSION,

    /**
     * Method of crashing by sending an invalid block change packet.
     */
    BLOCK
}
package com.deathmotion.playercrasher.enums;

import lombok.Getter;

/**
 * Enumeration representing the different methods to crash a player's game.
 */
@Getter
public enum CrashMethod {
    /**
     * Method of crashing by sending all available crash methods
     */
    ALL("All"),

    /**
     * Method of crashing by sending an invalid position packet.
     *
     * @see <a href="https://wiki.vg/Protocol">Synchronize Player Position (Packet ID: 0x3E)</a>
     */
    POSITION("Position"),

    /**
     * Method of crashing by sending an invalid explosion packet.
     *
     * @see <a href="https://wiki.vg/Protocol">Explosion (Packet ID: 0x1E)</a>
     */
    EXPLOSION("Explosion"),

    /**
     * Method of crashing by sending an invalid block change packet.
     *
     * @see <a href="https://wiki.vg/Protocol">Particle (Packet ID: 0x27)</a>
     */
    PARTICLE("Particle");

    private final String properName;

    CrashMethod(String properName) {
        this.properName = properName;
    }
}
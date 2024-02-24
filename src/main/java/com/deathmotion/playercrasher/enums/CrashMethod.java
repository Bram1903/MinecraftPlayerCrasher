package com.deathmotion.playercrasher.enums;

/**
 * Enumeration representing the different methods to crash a player's game.
 */
public enum CrashMethod {
    /**
     * Method of crashing by sending an invalid position packet.
     *
     * @see <a href="https://wiki.vg/Protocol">Synchronize Player Position (Packet ID: 0x3E)</a>
     */
    POSITION,

    /**
     * Method of crashing by sending an invalid explosion packet.
     *
     * @see <a href="https://wiki.vg/Protocol">Explosion (Packet ID: 0x1E)</a>
     */
    EXPLOSION,

    /**
     * Method of crashing by sending an invalid block change packet.
     *
     * @see <a href="https://wiki.vg/Protocol">Particle (Packet ID: 0x27)</a>
     */
    PARTICLE
}
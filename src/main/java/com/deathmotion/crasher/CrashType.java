package com.deathmotion.crasher;

public enum CrashType {
    EXPLOSION,
    POSITION,
    ENTITY;

    public static CrashType getFromString(String s) {

        for (CrashType type : values()) {
            if (type.name().toLowerCase().contains(s.toLowerCase())) {
                return type;
            }
        }

        return null;

    }

}
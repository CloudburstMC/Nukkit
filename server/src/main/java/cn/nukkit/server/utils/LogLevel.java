package cn.nukkit.server.utils;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public enum LogLevel implements Comparable<LogLevel> {
    NONE,
    EMERGENCY,
    ALERT,
    CRITICAL,
    ERROR,
    WARNING,
    NOTICE,
    INFO,
    DEBUG;
    public static final LogLevel DEFAULT_LEVEL = INFO;

    int getLevel() {
        return ordinal();
    }
}

package cn.nukkit.utils;

import java.util.function.BiConsumer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public enum LogLevel implements Comparable<LogLevel> {
    NONE((logger, message) -> {
    }),
    EMERGENCY(MainLogger::emergency),
    ALERT(MainLogger::alert),
    CRITICAL(MainLogger::critical),
    ERROR(MainLogger::error),
    WARNING(MainLogger::warning),
    NOTICE(MainLogger::notice),
    INFO(MainLogger::info),
    DEBUG(MainLogger::debug);

    public static final LogLevel DEFAULT_LEVEL = INFO;

    private final BiConsumer<MainLogger, String> logTo;

    LogLevel(BiConsumer<MainLogger, String> logTo) {
        this.logTo = logTo;
    }

    public void log(MainLogger logger, String message) {
        logTo.accept(logger, message);
    }

    public int getLevel() {
        return ordinal();
    }
}

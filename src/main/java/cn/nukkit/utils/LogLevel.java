package cn.nukkit.utils;

import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiConsumer;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public enum LogLevel implements Comparable<LogLevel> {
    NONE((logger, message) -> {}, (mainLogger, s, throwable) -> {}),
    EMERGENCY(MainLogger::emergency, MainLogger::emergency),
    ALERT(MainLogger::alert, MainLogger::alert),
    CRITICAL(MainLogger::critical, MainLogger::critical),
    ERROR(MainLogger::error, MainLogger::error),
    WARNING(MainLogger::warning, MainLogger::warning),
    NOTICE(MainLogger::notice, MainLogger::notice),
    INFO(MainLogger::info, MainLogger::info),
    DEBUG(MainLogger::debug, MainLogger::debug);

    public static final LogLevel DEFAULT_LEVEL = INFO;

    private final BiConsumer<MainLogger, String> logTo;
    private final TriConsumer<MainLogger, String, Throwable> logThrowableTo;

    LogLevel(BiConsumer<MainLogger, String> logTo, TriConsumer<MainLogger, String, Throwable> logThrowableTo) {
        this.logTo = logTo;
        this.logThrowableTo = logThrowableTo;
    }

    public void log(MainLogger logger, String message) {
        logTo.accept(logger, message);
    }

    public void log(MainLogger logger, String message, Throwable throwable) {
        logThrowableTo.accept(logger, message, throwable);
    }

    public int getLevel() {
        return ordinal();
    }
}

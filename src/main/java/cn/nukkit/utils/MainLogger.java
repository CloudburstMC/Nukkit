package cn.nukkit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * author: MagicDroidX
 * Nukkit
 */
/*
We need to keep this class for backwards compatibility
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MainLogger extends ThreadedLogger {

    private static final MainLogger logger = new MainLogger();

    public static MainLogger getLogger() {
        return logger;
    }

    @Override
    public void emergency(String message) {
        log.fatal(message);
    }

    @Override
    public void alert(String message) {
        log.error(message);
    }

    @Override
    public void critical(String message) {
        log.fatal(message);
    }

    @Override
    public void error(String message) {
        log.error(message);
    }

    @Override
    public void warning(String message) {
        log.warn(message);
    }

    @Override
    public void notice(String message) {
        log.warn(message);
    }

    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void debug(String message) {
        log.debug(message);
    }

    public void setLogDebug(Boolean logDebug) {
        throw new UnsupportedOperationException();
    }

    public void logException(Exception e) {
        log.error(e);
    }

    @Override
    public void log(LogLevel level, String message) {
        level.log(this, message);
    }

    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void emergency(String message, Throwable t) {
        log.fatal(message, t);
    }

    @Override
    public void alert(String message, Throwable t) {
        log.error(message, t);
    }

    @Override
    public void critical(String message, Throwable t) {
        log.fatal(message, t);
    }

    @Override
    public void error(String message, Throwable t) {
        log.error(message, t);
    }

    @Override
    public void warning(String message, Throwable t) {
        log.warn(message, t);
    }

    @Override
    public void notice(String message, Throwable t) {
        log.warn(message, t);
    }

    @Override
    public void info(String message, Throwable t) {
        log.info(message, t);
    }

    @Override
    public void debug(String message, Throwable t) {
        log.debug(message, t);
    }

    @Override
    public void log(LogLevel level, String message, Throwable t) {
        this.log(level, message + "\r\n" + Utils.getExceptionMessage(t));
    }
}

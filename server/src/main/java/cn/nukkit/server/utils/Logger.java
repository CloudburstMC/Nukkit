package cn.nukkit.server.utils;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Logger {

    void emergency(String message);

    void alert(String message);

    void critical(String message);

    void error(String message);

    void warning(String message);

    void notice(String message);

    void info(String message);

    void debug(String message);

    void log(LogLevel level, String message);

    void emergency(String message, Throwable t);

    void alert(String message, Throwable t);

    void critical(String message, Throwable t);

    void error(String message, Throwable t);

    void warning(String message, Throwable t);

    void notice(String message, Throwable t);

    void info(String message, Throwable t);

    void debug(String message, Throwable t);

    void log(LogLevel level, String message, Throwable t);
}

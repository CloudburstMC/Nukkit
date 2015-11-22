package cn.nukkit.utils;

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
}

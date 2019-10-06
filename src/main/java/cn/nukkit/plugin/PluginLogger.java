package cn.nukkit.plugin;

import cn.nukkit.utils.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginLogger implements Logger {

    private final org.apache.logging.log4j.Logger log;
    private final String pluginName;

    public PluginLogger(Plugin context) {
        String prefix = context.getDescription().getPrefix();
        this.pluginName = prefix != null ? "[" + prefix + "] " : "[" + context.getDescription().getName() + "] ";
        this.log = LogManager.getLogger(context.getDescription().getFullName());
    }

    @Override
    public void emergency(String message) {
        this.log(Level.FATAL, message);
    }

    @Override
    public void alert(String message) {
        this.log(Level.INFO, message);
    }

    @Override
    public void critical(String message) {
        this.log(Level.FATAL, message);
    }

    @Override
    public void error(String message) {
        this.log(Level.ERROR, message);
    }

    @Override
    public void warning(String message) {
        this.log(Level.WARN, message);
    }

    @Override
    public void notice(String message) {
        this.log(Level.INFO, message);
    }

    @Override
    public void info(String message) {
        this.log(Level.INFO, message);
    }

    @Override
    public void debug(String message) {
        this.log(Level.DEBUG, message);
    }

    @Override
    public void log(Level level, String message) {
        log.log(level, this.pluginName + message);
    }

    @Override
    public void emergency(String message, Throwable t) {
        this.log(Level.FATAL, message, t);
    }

    @Override
    public void alert(String message, Throwable t) {
        this.log(Level.INFO, message, t);
    }

    @Override
    public void critical(String message, Throwable t) {
        this.log(Level.FATAL, message, t);
    }

    @Override
    public void error(String message, Throwable t) {
        this.log(Level.ERROR, message, t);
    }

    @Override
    public void warning(String message, Throwable t) {
        this.log(Level.WARN, message, t);
    }

    @Override
    public void notice(String message, Throwable t) {
        this.log(Level.INFO, message, t);
    }

    @Override
    public void info(String message, Throwable t) {
        this.log(Level.INFO, message, t);
    }

    @Override
    public void debug(String message, Throwable t) {
        this.log(Level.DEBUG, message, t);
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        log.log(level, this.pluginName + message, t);
    }
}

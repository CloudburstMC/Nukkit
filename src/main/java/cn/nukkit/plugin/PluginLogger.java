package cn.nukkit.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public final class PluginLogger implements Logger {

    private final Logger logger;
    private final String pluginName;

    public PluginLogger(Plugin context) {
        String prefix = context.getDescription().getPrefix();
        this.pluginName = prefix != null ? "[" + prefix + "] " : "[" + context.getDescription().getName() + "] ";
        this.logger = LoggerFactory.getLogger(context.getDescription().getFullName());
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String s) {
        logger.trace(this.pluginName + s);
    }

    @Override
    public void trace(String s, Object o) {
        this.logger.trace(this.pluginName + s, o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        this.logger.trace(this.pluginName + s, o, o1);
    }

    @Override
    public void trace(String s, Object... objects) {
        this.logger.trace(this.pluginName + s, objects);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        this.logger.trace(this.pluginName + s, throwable);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return this.logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String s) {
        this.logger.trace(marker, this.pluginName + s);
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        this.logger.trace(marker, this.pluginName + s, o);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        this.logger.trace(marker, this.pluginName + s, o, o1);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        this.logger.trace(marker, this.pluginName + s, objects);
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        this.logger.trace(marker, this.pluginName + s, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        logger.debug(this.pluginName + s);
    }

    @Override
    public void debug(String s, Object o) {
        this.logger.debug(this.pluginName + s, o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        this.logger.debug(this.pluginName + s, o, o1);
    }

    @Override
    public void debug(String s, Object... objects) {
        this.logger.debug(this.pluginName + s, objects);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        this.logger.debug(this.pluginName + s, throwable);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return this.logger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String s) {
        this.logger.debug(marker, this.pluginName + s);
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        this.logger.debug(marker, this.pluginName + s, o);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        this.logger.debug(marker, this.pluginName + s, o, o1);
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        this.logger.debug(marker, this.pluginName + s, objects);
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        this.logger.debug(marker, this.pluginName + s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        logger.info(this.pluginName + s);
    }

    @Override
    public void info(String s, Object o) {
        this.logger.info(this.pluginName + s, o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        this.logger.info(this.pluginName + s, o, o1);
    }

    @Override
    public void info(String s, Object... objects) {
        this.logger.info(this.pluginName + s, objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        this.logger.info(this.pluginName + s, throwable);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return this.logger.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String s) {
        this.logger.info(marker, this.pluginName + s);
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        this.logger.info(marker, this.pluginName + s, o);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        this.logger.info(marker, this.pluginName + s, o, o1);
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        this.logger.info(marker, this.pluginName + s, objects);
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        this.logger.info(marker, this.pluginName + s, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String s) {
        logger.warn(this.pluginName + s);
    }

    @Override
    public void warn(String s, Object o) {
        this.logger.warn(this.pluginName + s, o);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        this.logger.warn(this.pluginName + s, o, o1);
    }

    @Override
    public void warn(String s, Object... objects) {
        this.logger.warn(this.pluginName + s, objects);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        this.logger.warn(this.pluginName + s, throwable);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return this.logger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String s) {
        this.logger.warn(marker, this.pluginName + s);
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        this.logger.warn(marker, this.pluginName + s, o);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        this.logger.warn(marker, this.pluginName + s, o, o1);
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        this.logger.warn(marker, this.pluginName + s, objects);
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        this.logger.warn(marker, this.pluginName + s, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String s) {
        logger.error(this.pluginName + s);
    }

    @Override
    public void error(String s, Object o) {
        this.logger.error(this.pluginName + s, o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        this.logger.error(this.pluginName + s, o, o1);
    }

    @Override
    public void error(String s, Object... objects) {
        this.logger.error(this.pluginName + s, objects);
    }

    @Override
    public void error(String s, Throwable throwable) {
        this.logger.error(this.pluginName + s, throwable);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return this.logger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String s) {
        this.logger.error(marker, this.pluginName + s);
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        this.logger.error(marker, this.pluginName + s, o);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        this.logger.error(marker, this.pluginName + s, o, o1);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        this.logger.error(marker, this.pluginName + s, objects);
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        this.logger.error(marker, this.pluginName + s, throwable);
    }
}

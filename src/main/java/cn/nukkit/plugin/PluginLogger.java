package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.utils.LogLevel;
import cn.nukkit.utils.Logger;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginLogger implements Logger {

    private String pluginName;

    public PluginLogger(Plugin context) {
        String prefix = context.getDescription().getPrefix();
        this.pluginName = prefix != null ? "[" + prefix + "] " : "[" + context.getDescription().getName() + "] ";
    }

    @Override
    public void emergency(String message) {
        this.log(LogLevel.EMERGENCY, message);
    }

    @Override
    public void alert(String message) {
        this.log(LogLevel.ALERT, message);
    }

    @Override
    public void critical(String message) {
        this.log(LogLevel.CRITICAL, message);
    }

    @Override
    public void error(String message) {
        this.log(LogLevel.ERROR, message);
    }

    @Override
    public void warning(String message) {
        this.log(LogLevel.WARNING, message);
    }

    @Override
    public void notice(String message) {
        this.log(LogLevel.NOTICE, message);
    }

    @Override
    public void info(String message) {
        this.log(LogLevel.INFO, message);
    }

    @Override
    public void debug(String message) {
        this.log(LogLevel.DEBUG, message);
    }

    @Override
    public void log(LogLevel level, String message) {
        Server.getInstance().getLogger().log(level, this.pluginName + message);
    }

}

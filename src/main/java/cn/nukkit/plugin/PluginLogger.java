package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.utils.MainLogger;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginLogger {

    private String pluginName;

    public PluginLogger(Plugin context) {
        String prefix = context.getDescription().getPrefix();
        this.pluginName = prefix != null ? "[" + prefix + "] " : "[" + context.getDescription().getName() + "] ";
    }

    public void emergency(String message) {
        this.log(MainLogger.EMERGENCY, message);
    }

    public void alert(String message) {
        this.log(MainLogger.ALERT, message);
    }

    public void critical(String message) {
        this.log(MainLogger.CRITICAL, message);
    }

    public void error(String message) {
        this.log(MainLogger.ERROR, message);
    }

    public void warning(String message) {
        this.log(MainLogger.WARNING, message);
    }

    public void notice(String message) {
        this.log(MainLogger.NOTICE, message);
    }

    public void info(String message) {
        this.log(MainLogger.INFO, message);
    }

    public void debug(String message) {
        this.log(MainLogger.DEBUG, message);
    }

    public void log(String level, String message) {
        Server.getInstance().getLogger().log(level, this.pluginName + message);
    }
}

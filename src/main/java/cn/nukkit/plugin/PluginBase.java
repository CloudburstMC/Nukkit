package cn.nukkit.plugin;

import cn.nukkit.Server;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
abstract public class PluginBase implements Plugin {

    private PluginLoader loader;
    private Server server;
    private boolean isEnabled = false;
    private boolean initialized = false;
    private PluginDescription description;
    private String dataFolder;
    private String config;
    private String configFile;
    private String file;
    private PluginLogger logger;


    public void onLoad() {

    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public final boolean isEnabled() {
        return isEnabled;
    }

    public final void setEnabled(boolean value) {
        if (isEnabled != value) {
            isEnabled = value;
            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    public final boolean isDisabled() {
        return !isEnabled;
    }

    public final String getDataFolder() {
        return dataFolder;
    }

    public final PluginDescription getDescription() {
        return description;
    }

    public final void init(PluginLoader loader, Server server, PluginDescription description, String dataFolder, String file) {
        if (!initialized) {
            initialized = true;
            loader = loader;
            server = server;
            description = description;
            this.dataFolder = dataFolder;
            if (!(dataFolder.endsWith("\\") || dataFolder.endsWith("/"))) {
                this.dataFolder = dataFolder + "\\";
            }

            this.file = file;
            if (!(file.endsWith("\\") || file.endsWith("/"))) {
                this.file = file + "\\";
            }
            configFile = this.dataFolder + "config.yml";
            logger = new PluginLogger(this);
        }
    }

    public PluginLogger getLogger() {
        return logger;
    }

    public final boolean isInitialized() {
        return initialized;
    }

}

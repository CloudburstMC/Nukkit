package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.utils.Config;

import java.io.File;
import java.io.InputStream;

/**
 * author: MagicDroidX
 * Nukkit
 */
public interface Plugin {

    void onLoad();

    void onEnable();

    boolean isEnabled();

    void onDisable();

    boolean isDisabled();

    File getDataFolder();

    PluginDescription getDescription();

    InputStream getResource(String filename);

    void saveResource(String filename);

    void saveResource(String filename, boolean replace);

    Config getConfig();

    void saveConfig();

    void saveDefaultConfig();

    void reloadConfig();

    Server getServer();

    String getName();

    PluginLogger getLogger();

    PluginLoader getPluginLoader();

}

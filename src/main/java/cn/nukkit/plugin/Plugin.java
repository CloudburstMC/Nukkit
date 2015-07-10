package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.utils.Config;

/**
 * author: MagicDroidX
 * Nukkit
 */
public interface Plugin {
    //todo
    void onLoad();

    void onEnable();

    boolean isEnabled();

    void onDisable();

    boolean isDisabled();

    String getDataFolder();

    //todo a lot...

    Config getConfig();

    void saveConfig();

    void saveDefaultConfig();

    void reloadConfig();

    Server getServer();

    String getName();

    //todo a lot...
}

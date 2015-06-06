package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.utils.Config;

/**
 * author: MagicDroidX
 * Nukkit
 */
public abstract interface Plugin {
    //todo
    public abstract void onLoad();

    public abstract void onEnable();

    public abstract boolean isEnabled();

    public abstract void onDisable();

    public abstract boolean isDisabled();

    public abstract String getDataFolder();

    //todo a lot...

    public abstract Config getConfig();

    public abstract void saveConfig();

    public abstract void saveDefaultConfig();

    public abstract void reloadConfig();

    public abstract Server getServer();

    public abstract String getName();

    //todo a lot...
}

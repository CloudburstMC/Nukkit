package cn.nukkit.plugin;

import java.util.regex.Pattern;

/**
 * author: iNevet
 * Nukkit Project
 */
public interface PluginLoader {

    Plugin loadPlugin(String filename) throws Exception;

    PluginDescription getPluginDescription(String filename);

    Pattern[] getPluginFilters();

    void enablePlugin(Plugin plugin);

    void disablePlugin(Plugin plugin);

}

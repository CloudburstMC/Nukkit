package cn.nukkit.plugin;

import java.util.regex.Pattern;

/**
 * author: iNevet
 * Nukkit Project
 */
public interface PluginLoader {

    Plugin loadPlugin(String file) throws Exception;

    PluginDescription getPluginDescription(String file);

    Pattern[] getPluginFilters();

    void enablePlugin(Plugin plugin);

    void disablePlugin(Plugin plugin);

}

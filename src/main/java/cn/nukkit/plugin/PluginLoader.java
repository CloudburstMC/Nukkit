package cn.nukkit.plugin;

import java.io.File;
import java.util.regex.Pattern;

/**
 * author: iNevet
 * Nukkit Project
 */
public interface PluginLoader {

    Plugin loadPlugin(String filename) throws Exception;

    Plugin loadPlugin(File file) throws Exception;

    PluginDescription getPluginDescription(String filename);

    PluginDescription getPluginDescription(File file);

    Pattern[] getPluginFilters();

    void enablePlugin(Plugin plugin);

    void disablePlugin(Plugin plugin);

}

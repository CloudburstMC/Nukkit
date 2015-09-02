package cn.nukkit.plugin;

/**
 * Created by Nukkit Team.
 */
public abstract class PluginLoader {

    public abstract Plugin loadPlugin(String file) throws Exception;

    public abstract PluginDescription getPluginDescription(String file);

    public abstract void enablePlugin(Plugin plugin);

    public abstract void disablePlugin(Plugin plugin);

}

package cn.nukkit.event.plugin;

import cn.nukkit.plugin.Plugin;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PluginEnableEvent extends PluginEvent {

    public PluginEnableEvent(Plugin plugin) {
        super(plugin);
    }
}

package cn.nukkit.server.event.plugin;

import cn.nukkit.server.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginEnableEvent extends PluginEvent {

    public PluginEnableEvent(Plugin plugin) {
        super(plugin);
    }
}

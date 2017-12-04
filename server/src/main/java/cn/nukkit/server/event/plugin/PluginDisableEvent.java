package cn.nukkit.server.event.plugin;

import cn.nukkit.server.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginDisableEvent extends PluginEvent {

    public PluginDisableEvent(Plugin plugin) {
        super(plugin);
    }
}

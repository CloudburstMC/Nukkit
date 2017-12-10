package cn.nukkit.api.event.plugin;

import cn.nukkit.api.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginDisableEvent extends PluginEvent {

    public PluginDisableEvent(Plugin plugin) {
        super(plugin);
    }
}

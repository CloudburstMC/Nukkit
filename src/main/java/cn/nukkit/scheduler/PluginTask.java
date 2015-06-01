package cn.nukkit.scheduler;


import cn.nukkit.plugin.PluginBase;

/**
 * author: MagicDroidX
 * Nukkit
 */
abstract class PluginTask extends Task {

    protected PluginBase owner;

    public PluginTask(PluginBase owner) {
        this.owner = owner;
    }

    public final PluginBase getOwner() {
        return this.owner;
    }

}

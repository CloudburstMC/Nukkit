package cn.nukkit.scheduler;


import cn.nukkit.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit
 */
abstract class PluginTask extends Task {

    protected Plugin owner;

    public PluginTask(Plugin owner) {
        this.owner = owner;
    }

    public final Plugin getOwner() {
        return this.owner;
    }

}

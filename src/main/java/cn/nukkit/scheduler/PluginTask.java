package cn.nukkit.scheduler;


import cn.nukkit.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit
 */
public abstract class PluginTask<T extends Plugin> extends Task {

    protected T owner;

    public PluginTask(T owner) {
        this.owner = owner;
    }

    public final T getOwner() {
        return this.owner;
    }

}

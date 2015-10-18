package cn.nukkit.scheduler;


import cn.nukkit.plugin.Plugin;

/**
 * 插件创建的任务。<br>Task that created by a plugin.
 *
 * TODO 完善
 *
 * @param <T> 这个任务所属的插件。<br>The plugin that owns this task.
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
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

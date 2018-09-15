package cn.nukkit.scheduler;


import cn.nukkit.plugin.Plugin;

/**
 * 插件创建的任务。<br>Task that created by a plugin.
 *
 * <p>对于插件作者，通过继承这个类创建的任务，可以在插件被禁用时不被执行。<br>
 * For plugin developers: Tasks that extend this class, won't be executed when the plugin is disabled.</p>
 *
 * <p>另外，继承这个类的任务可以通过{@link #getOwner()}来获得这个任务所属的插件。<br>
 * Otherwise, tasks that extend this class can use {@link #getOwner()} to get its owner.</p>
 *
 * 下面是一个插件创建任务的例子：<br>An example for plugin create a task:
 * <pre>
 *     public class ExampleTask extends PluginTask&lt;ExamplePlugin&gt;{
 *         public ExampleTask(ExamplePlugin plugin){
 *             super(plugin);
 *         }
 *
 *        {@code @Override}
 *         public void onRun(int currentTick){
 *             getOwner().getLogger().info("Task is executed in tick "+currentTick);
 *         }
 *     }
 * </pre>
 *
 * <p>如果要让Nukkit能够延时或循环执行这个任务，请使用{@link ServerScheduler}。<br>
 * If you want Nukkit to execute this task with delay or repeat, use {@link ServerScheduler}.</p>
 *
 * @param <T> 这个任务所属的插件。<br>The plugin that owns this task.
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public abstract class PluginTask<T extends Plugin> extends Task {

    protected final T owner;

    /**
     * 构造一个插件拥有的任务的方法。<br>Constructs a plugin-owned task.
     *
     * @param owner 这个任务的所有者插件。<br>The plugin object that owns this task.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public PluginTask(T owner) {
        this.owner = owner;
    }

    /**
     * 返回这个任务的所有者插件。<br>
     * Returns the owner of this task.
     *
     * @return 这个任务的所有者插件。<br>The plugin that owns this task.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public final T getOwner() {
        return this.owner;
    }

}

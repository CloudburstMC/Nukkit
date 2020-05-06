package cn.nukkit.command;

import cn.nukkit.command.data.CommandData;
import cn.nukkit.plugin.Plugin;

/**
 * This class is used as a base for all Commands for Plugins. You will want to extend this class to implement
 * your custom commands, and then register them with the {@link cn.nukkit.registry.CommandRegistry CommandRegistry}
 * in your {@link cn.nukkit.plugin.PluginBase Plugin}'s <code>onLoad()</code> method to ensure you are registering
 * them before the registration period closes.<p/>
 * The {@link CommandData} created during the constructor cannot be modified at runtime. For simplicity, a
 * {@link CommandData.Builder Builder} class has been provided to make construction of custom Commands easier.
 * <p/>
 * Here is an example of implementation of a PluginCommand for a Plugin with base class <code>MyPlugin</code>:
 * <pre>
 * {@code
 * public class MyPluginCommand extends PluginCommand<MyPlugin> {
 *      public MyPluginCommand(MyPlugin plugin) {
 *          super(plugin, CommandData.builder("mycommand")
 *              .setDescription("This is my awesome Command!")
 *              .setUsage("/mycommand &lt;arg&gt; [optionalArg]")
 *              .setPermission("com.example.mycommand")
 *              .build());
 *
 *             // Here you can optionally set a CommandExecutor that will be called on the command run
 *             // by default, the command Executor will be the owning Plugin (MyPlugin)
 *      }
 * }
 * }</pre>
 * By default, the PluginBase (<code>MyPlugin</code> in example above) is the {@link CommandExecutor}.
 * You may implement the CommandExecutor interface on the PluginCommand itself if you wish, and then add
 * <code>this.setExecutor(this)</code> after the <code>super()</code> call in the Constructor.<p/>
 * Wherever you have the CommandExecutor implemented, you will want to have the following: <pre>
 * {@code
 *      @Override
 *      public boolean onCommand(CommandSender sender, Command command, commandLabel, args) {
 *          // If you do not define a CommandExecutor for the Command either in the
 *          // constructor or at time of registration, the owning Plugin will be used as the executor
 *
 *          // Do your command stuff here
 *          // Note that a base permissions check will already have been done at this point,
 *          // and if you return false, the server will send the Usage message to the sender
 *      }
 * }
 * </pre>
 * To register the command (recommended to be done from your PluginBase class <code>onLoad()</code>), you can do the following:
 * <pre>
 * {@code getServer().getCommandRegistry().register(this, new MyCommand(this);}
 * </pre>
 *
 * @author MagicDroidX
 * @see CommandExecutor#onCommand
 */
public class PluginCommand<T extends Plugin> extends Command implements PluginIdentifiableCommand {

    private final T owningPlugin;

    private CommandExecutor executor;

    public PluginCommand(T owner, CommandData data) {
        super(data);
        this.owningPlugin = owner;
        this.executor = owner;
    }

    /**
     * It is not recommended to override this method, but instead to implement the {@link CommandExecutor}
     * (by default the owning Plugin of this command). This way you will have a built-in pre-check of if
     * your plugin is enabled, the sender has permission, and will show the Usage message to the sender
     * if your {@link CommandExecutor#onCommand onCommand()} returns false;
     * <p>
     * Alternatively, you may override this method in your Command class, but will need to check the sender's
     * permissions and ensure your plugin is enabled prior to running your command code. Returning <code>false</code>
     * will still show the Usage (if not empty) to the sender. If you already sent feedback to the sender,
     * you may return <code>true</code>.
     *
     * @param sender       Origin of the command
     * @param commandLabel Alias that was used to call the command
     * @param args         Command line arguments
     * @return true on successful processing, false otherwise (which will show Usage message to the sender)
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.owningPlugin.isEnabled()) {
            return false;
        }

        if (!this.testPermission(sender)) {
            return false;
        }

        return this.executor.onCommand(sender, this, commandLabel, args);
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = (executor != null) ? executor : this.owningPlugin;
    }

    @Override
    public T getPlugin() {
        return this.owningPlugin;
    }
}

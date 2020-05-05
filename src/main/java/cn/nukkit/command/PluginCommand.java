package cn.nukkit.command;

import cn.nukkit.command.data.CommandData;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.plugin.Plugin;

/**
 * This class is used as a base for all Commands for Plugins. You will want to extend this class to implement
 * your custom commands, and then register them with the {@link cn.nukkit.registry.CommandRegistry} in your
 * {@link cn.nukkit.plugin.PluginBase Plugin}'s <code>onLoad()</code> method to ensure you are registering them
 * before the registration period closes.<p/>
 * The {@link CommandData} created during the constructor cannot be modified at runtime. For simplicity, a
 * {@link CommandData.Builder Builder} class has been provided to make construction of custom Commands easier.
 * <p/>
 * Here is an example of implementation of a PluginCommand for a Plugin with base class <code>MyPlugin</code>:
 * <pre>
 * {@code
 * public class MyPluginCommand extends PluginCommand<MyPlugin> {
 *      public MyPluginCommand(MyPlugin plugin) {
 *          super("mycommand", CommandData.builder("mycommand")
 *              .setDescription("This is my awesome Command!")
 *              .setUsage("/mycommand &lt;arg&gt; [optionalArg]")
 *              .setPermission("com.example.mycommand")
 *              .build(),
 *              plugin);
 *
 *             // Here you can optionally set a CommandExecutor that will be called on the command run
 *      }
 *
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

    public PluginCommand(String name, CommandData data, T owner) {
        super(name, data);
        this.owningPlugin = owner;
        this.executor = owner;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.owningPlugin.isEnabled()) {
            return false;
        }

        if (!this.testPermission(sender)) {
            return false;
        }

        boolean success = this.executor.onCommand(sender, this, commandLabel, args);

        if (!success && !this.getUsage().equals("")) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.getUsage()));
        }

        return success;
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

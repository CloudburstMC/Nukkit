package cn.nukkit.command;

import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginCommand<T extends Plugin> extends Command implements PluginIdentifiableCommand {

    private final T owningPlugin;

    private CommandExecutor executor;

    public PluginCommand(String name, T owner) {
        super(name);
        this.owningPlugin = owner;
        this.executor = owner;
        this.usageMessage = "";
    }

    public PluginCommand(String name, T owner, String description, String usage, String perm, String permMsg, String[] aliases) {
        this(name, owner);
        this.description = description;
        this.usageMessage = usage;
        this.setPermission(perm);
        this.setPermissionMessage(permMsg);
        this.setAliases(aliases);
    }

    public static CommandFactory factory(Plugin owner, String desc, String usage, String perm, String permMsg, String[] aliases) {
        return name -> new PluginCommand<>(name, owner, desc, usage, perm, permMsg, aliases);
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

        if (!success && !this.usageMessage.equals("")) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
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

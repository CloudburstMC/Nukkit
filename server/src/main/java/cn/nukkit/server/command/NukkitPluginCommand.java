package cn.nukkit.server.command;

import cn.nukkit.api.MessageRecipient;
import cn.nukkit.api.command.CommandExecutor;
import cn.nukkit.api.command.CommandExecutorSource;
import cn.nukkit.api.command.PluginCommand;
import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.api.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NukkitPluginCommand<T extends Plugin> extends NukkitCommand implements PluginCommand {

    private final T owningPlugin;

    private CommandExecutor executor;

    public NukkitPluginCommand(String name, T owner) {
        super(name);
        this.owningPlugin = owner;
        this.executor = owner;
        this.usageMessage = "";
    }

    @Override
    public boolean execute(CommandExecutorSource sender, String commandLabel, String[] args) {
        if (!this.owningPlugin.isEnabled()) {
            return false;
        }

        if (!this.testPermission(sender)) {
            return false;
        }

        boolean success = this.executor.onCommand(sender, this, commandLabel, args);

        if ((sender instanceof MessageRecipient) && !success && !this.usageMessage.equals("")) {
            ((MessageRecipient) sender).sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
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

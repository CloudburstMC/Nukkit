package cn.nukkit.command;

import cn.nukkit.event.TranslationContainer;
import cn.nukkit.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginCommand extends Command implements PluginIdentifiableCommand {

    private Plugin owningPlugin;

    private CommandExecutor executor;

    public PluginCommand(String name, Plugin owner) {
        super(name);
        this.owningPlugin = owner;
        this.executor = owner;
        this.usageMessage = "";
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
    public Plugin getPlugin() {
        return this.owningPlugin;
    }
}

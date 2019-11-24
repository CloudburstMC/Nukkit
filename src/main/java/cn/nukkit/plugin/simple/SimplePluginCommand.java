package cn.nukkit.plugin.simple;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimplePluginCommand extends PluginCommand {

    private Method commandMethod;

    public SimplePluginCommand(String name, Plugin owner) {
        super(name, owner);
    }

    public void setCommandMethod(Method commandMethod) {
        this.commandMethod = commandMethod;
    }


    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        try {
            if (!this.owningPlugin.isEnabled()) {
                return false;
            }

            if (!this.testPermission(sender)) {
                return false;
            }

            boolean success = (Boolean) commandMethod.invoke(owningPlugin, sender, this, commandLabel, args);

            if (!success && !this.usageMessage.equals("")) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            }

            return success;
        }catch (IllegalAccessException| InvocationTargetException e){
            e.printStackTrace();
        }
        return false;
    }
}

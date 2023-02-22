package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2015/11/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class SaveOnCommand extends VanillaCommand {

    public SaveOnCommand(String name) {
        super(name, "%nukkit.command.saveon.description", "%commands.save-on.usage");
        this.setPermission("nukkit.command.save.enable");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String @NotNull [] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        sender.getServer().setAutoSave(true);
        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.save.enabled"));
        return true;
    }
}

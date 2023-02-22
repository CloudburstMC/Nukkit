package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2015/11/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class SaveOffCommand extends VanillaCommand {

    public SaveOffCommand(String name) {
        super(name, "%nukkit.command.saveoff.description", "%commands.save-off.usage");
        this.setPermission("nukkit.command.save.disable");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String @NotNull [] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        sender.getServer().setAutoSave(false);
        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.save.disabled"));
        return true;
    }
}

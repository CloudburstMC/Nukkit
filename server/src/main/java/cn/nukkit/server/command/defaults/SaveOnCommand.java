package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.command.Command;

/**
 * Created on 2015/11/13 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class SaveOnCommand extends VanillaCommand {

    public SaveOnCommand(String name) {
        super(name, "%nukkit.command.saveon.description", "%commands.save-on.usage");
        this.setPermission("nukkit.command.save.enable");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        sender.getServer().setAutoSave(true);
        Command.broadcastCommandMessage(sender, new TranslatedMessage("commands.save.enabled"));
        return true;
    }
}

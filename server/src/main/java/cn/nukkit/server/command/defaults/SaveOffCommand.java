package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.command.Command;

/**
 * Created on 2015/11/13 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class SaveOffCommand extends VanillaCommand {

    public SaveOffCommand(String name) {
        super(name, "%nukkit.command.saveoff.description", "%commands.save-off.usage");
        this.setPermission("nukkit.command.save.disable");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        sender.getServer().setAutoSave(false);
        Command.broadcastCommandMessage(sender, new TranslatedMessage("commands.save.disabled"));
        return true;
    }
}

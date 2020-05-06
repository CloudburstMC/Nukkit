package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.locale.TranslationContainer;

/**
 * Created on 2015/11/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class SaveOnCommand extends Command {

    public SaveOnCommand() {
        super("save-on", CommandData.builder("save-on")
                .setDescription("commands.save-on.description")
                .setPermissions("nukkit.command.save.enable")
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        sender.getServer().setAutoSave(true);
        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.save.enabled"));
        return true;
    }
}

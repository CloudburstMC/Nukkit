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
public class SaveOffCommand extends Command {

    public SaveOffCommand() {
        super("save-off", CommandData.builder("save-off")
                .setDescription("commands.save.description")
                .setPermissions("nukkit.command.save.disable")
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        sender.getServer().setAutoSave(false);
        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.save.disabled"));
        return true;
    }
}

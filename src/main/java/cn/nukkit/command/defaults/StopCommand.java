package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.locale.TranslationContainer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StopCommand extends Command {

    public StopCommand() {
        super("stop", CommandData.builder("stop")
                .setDescription("commands.stop.description")
                .setPermissions("nukkit.command.stop")
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.stop.start"));

        sender.getServer().shutdown();

        return true;
    }
}

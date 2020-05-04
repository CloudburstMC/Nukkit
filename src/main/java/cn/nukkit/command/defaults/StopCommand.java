package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.locale.TranslationContainer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StopCommand extends VanillaCommand {

    public StopCommand(String name) {
        super(name, "commands.stop.description", "/stop");
        this.setPermission("nukkit.command.stop");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.stop.start"));

        sender.getServer().shutdown();

        return true;
    }
}

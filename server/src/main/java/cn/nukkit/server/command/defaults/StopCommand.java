package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.command.Command;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StopCommand extends VanillaCommand {

    public StopCommand(String name) {
        super(name, "%nukkit.command.stop.description", "%commands.stop.usage");
        this.setPermission("nukkit.command.stop");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        Command.broadcastCommandMessage(sender, new TranslatedMessage("commands.stop.start"));

        sender.getServer().shutdown();

        return true;
    }
}

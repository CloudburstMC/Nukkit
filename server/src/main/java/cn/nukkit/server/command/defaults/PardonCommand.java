package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.command.Command;
import cn.nukkit.server.command.data.CommandParameter;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PardonCommand extends VanillaCommand {

    public PardonCommand(String name) {
        super(name, "%nukkit.command.unban.player.description", "%commands.unban.usage");
        this.setPermission("nukkit.command.unban.player");
        this.setAliases(new String[]{"unban"});
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParameter.ARG_TYPE_TARGET, false)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));

            return false;
        }

        sender.getServer().getNameBans().remove(args[0]);

        Command.broadcastCommandMessage(sender, new TranslatedMessage("%commands.unban.success", args[0]));

        return true;
    }
}

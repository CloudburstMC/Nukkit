package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.Player;
import cn.nukkit.server.command.data.CommandParameter;
import cn.nukkit.server.util.TextFormat;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class MeCommand extends VanillaCommand {

    public MeCommand(String name) {
        super(name, "%nukkit.command.me.description", "%commands.me.usage");
        this.setPermission("nukkit.command.me");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("action ...", CommandParameter.ARG_TYPE_RAW_TEXT, false)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));

            return false;
        }

        String name;
        if (sender instanceof Player) {
            name = ((Player) sender).getDisplayName();
        } else {
            name = sender.getName();
        }

        String msg = "";
        for (String arg : args) {
            msg += arg + " ";
        }

        if (msg.length() > 0) {
            msg = msg.substring(0, msg.length() - 1);
        }

        sender.getServer().broadcastMessage(new TranslatedMessage("chat.type.emote", new String[]{name, TextFormat.WHITE + msg}));

        return true;
    }
}

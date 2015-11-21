package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class SayCommand extends VanillaCommand {

    public SayCommand(String name) {
        super(name, "%nukkit.command.say.description", "%commands.say.usage");
        this.setPermission("nukkit.command.say");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        String senderString;
        if (sender instanceof Player) {
            senderString = ((Player) sender).getDisplayName();
        } else if (sender instanceof ConsoleCommandSender) {
            senderString = "Server";
        } else {
            senderString = sender.getName();
        }

        String msg = "";
        for (String arg : args) {
            msg += arg + " ";
        }
        if (msg.length() > 0) {
            msg = msg.substring(0, msg.length() - 1);
        }


        sender.getServer().broadcastMessage(new TranslationContainer(
                TextFormat.LIGHT_PURPLE + "%chat.type.announcement",
                new String[]{senderString, TextFormat.LIGHT_PURPLE + msg}
        ));
        return true;
    }
}

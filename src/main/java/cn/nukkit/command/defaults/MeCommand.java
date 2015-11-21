package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class MeCommand extends VanillaCommand {

    public MeCommand(String name) {
        super(name, "%nukkit.command.me.description", "%commands.me.usage");
        this.setPermission("nukkit.command.me");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", new String[]{this.usageMessage}));
            return false;
        }

        String senderString;
        if (sender instanceof Player) {
            senderString = ((Player) sender).getDisplayName();
        } else {
            senderString = sender.getName();
        }

        String msg = "";
        for (String arg : args) msg += arg + " ";

        TranslationContainer cont = new TranslationContainer(
                "chat.type.emote",
                new String[]{senderString, TextFormat.WHITE + msg}
        );
        sender.getServer().broadcastMessage(cont);
        return true;
    }
}

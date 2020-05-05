package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;


/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class MeCommand extends Command {

    public MeCommand() {
        super("me", CommandData.builder("me")
                .setDescription("commands.me.description")
                .setUsageMessage("/me <action>")
                .setPermissions("nukkit.command.me")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("action ...", CommandParamType.RAWTEXT, false)
                })
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            return false;
        }

        String name;
        if (sender instanceof Player) {
            name = ((Player) sender).getDisplayName();
        } else {
            name = sender.getName();
        }

        String msg = String.join(" ", args);
        sender.getServer().broadcastMessage(new TranslationContainer("chat.type.emote", name, TextFormat.WHITE + msg));

        return true;
    }
}

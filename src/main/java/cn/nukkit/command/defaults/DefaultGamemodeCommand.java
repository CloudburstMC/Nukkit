package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class DefaultGamemodeCommand extends Command {

    public DefaultGamemodeCommand() {
        super("defaultgamemode", CommandData.builder("defaultgamemode")
                .setDescription("commands.defaultgamemode.description")
                .setUsageMessage("/defaultgamemode <mode>")
                .setPermissions("nukkit.command.defaultgamemode")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("mode", CommandParamType.INT, false)
                }, new CommandParameter[]{
                        new CommandParameter("mode", new String[]{"survival", "creative", "s", "c",
                                "adventure", "a", "spectator", "view", "v"})
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
        int gameMode = Server.getGamemodeFromString(args[0]);
        if (gameMode != -1) {
            sender.getServer().setPropertyInt("gamemode", gameMode);
            sender.sendMessage(new TranslationContainer("commands.defaultgamemode.success", Server.getGamemodeString(gameMode)));
        } else {
            sender.sendMessage("Unknown game mode");
        }
        return true;
    }
}

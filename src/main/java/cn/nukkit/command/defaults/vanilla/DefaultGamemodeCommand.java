package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.locale.TranslationContainer;

import static cn.nukkit.command.args.builder.RequiredArgumentBuilder.requiredArg;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class DefaultGamemodeCommand extends VanillaCommand {

    public DefaultGamemodeCommand(String name) {
        super(name, "commands.defaultgamemode.description", "/defaultgamemode <mode>");
        this.setPermission("nukkit.command.defaultgamemode");

        registerOverload()
                .then(requiredArg("mode", CommandParamType.INT));
        registerOverload()
                .then(requiredArg("mode", "GameMode", new String[]{"sp", "spectator", "a", "adventure", "c", "creative", "s", "survival"}));
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", usageMessage));
            return false;
        }
        int gameMode = Server.getGamemodeFromString(args[0]);
        if (gameMode == -1) {
            sender.sendMessage(new TranslationContainer("commands.gamemode.fail.invalid", args[0]));
            return true;
        }

        sender.getServer().setPropertyInt("gamemode", gameMode);
        sender.sendMessage(new TranslationContainer("commands.defaultgamemode.success", Server.getGamemodeString(gameMode)));
        return true;
    }
}

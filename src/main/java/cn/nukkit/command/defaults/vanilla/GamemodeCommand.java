package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.args.builder.LiteralArgumentBuilder;
import cn.nukkit.command.args.builder.RequiredArgumentBuilder;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

import static cn.nukkit.command.args.builder.OptionalArgumentBuilder.optionalArg;
import static cn.nukkit.command.args.builder.RequiredArgumentBuilder.requiredArg;

/**
 * Created on 2015/11/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class GamemodeCommand extends VanillaCommand {

    public GamemodeCommand(String name) {
        super(name, "commands.gamemode.description", "/gamemode <mode> [player]",
                new String[]{"gm"});
        this.setPermission("nukkit.command.gamemode.survival;" +
                "nukkit.command.gamemode.creative;" +
                "nukkit.command.gamemode.adventure;" +
                "nukkit.command.gamemode.spectator;" +
                "nukkit.command.gamemode.other");

        registerOverload()
                .then(requiredArg("mode", "GameMode", new String[]{"sp", "spectator", "a", "adventure", "c", "creative", "s", "survival"}))
                .then(optionalArg("player", CommandParamType.TARGET));

        registerOverload()
                .then(requiredArg("mode", CommandParamType.INT))
                .then(optionalArg("player", CommandParamType.TARGET));
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        int gameMode = Server.getGamemodeFromString(args[0]);
        if (gameMode == -1) {
            sender.sendMessage(new TranslationContainer("commands.gamemode.fail.invalid", args[0]));
            return true;
        }

        CommandSender target = sender;
        if (args.length > 1) {
            if (sender.hasPermission("nukkit.command.gamemode.other")) {
                target = sender.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                    return true;
                }
            } else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
                return true;
            }
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }

        if ((gameMode == 0 && !sender.hasPermission("nukkit.command.gamemode.survival")) ||
                (gameMode == 1 && !sender.hasPermission("nukkit.command.gamemode.creative")) ||
                (gameMode == 2 && !sender.hasPermission("nukkit.command.gamemode.adventure")) ||
                (gameMode == 3 && !sender.hasPermission("nukkit.command.gamemode.spectator"))) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
            return true;
        }

        if (!((Player) target).setGamemode(gameMode)) {
            sender.sendMessage("Game mode update for " + target.getName() + " failed");
        } else {
            if (target.equals(sender)) {
                CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(gameMode)));
            } else {
                target.sendMessage(new TranslationContainer("gameMode.changed"));
                CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.gamemode.success.other", target.getName(), Server.getGamemodeString(gameMode)));
            }
        }

        return true;
    }
}

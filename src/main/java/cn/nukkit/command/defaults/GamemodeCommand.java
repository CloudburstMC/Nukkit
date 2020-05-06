package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class GamemodeCommand extends Command {

    public GamemodeCommand() {
        super("gamemode", CommandData.builder("gamemode")
                .setDescription("commands.gamemode.description")
                .setUsageMessage("/gamemode <mode> [player]")
                .setAliases("gm")
                .addPermission("nukkit.command.gamemode.survival")
                .addPermission("nukkit.command.gamemode.creative")
                .addPermission("nukkit.command.gamemode.adventure")
                .addPermission("nukkit.command.gamemode.spectator")
                .addPermission("nukkit.command.gamemode.other")
                .setParameters(
                        new CommandParameter[]{
                                new CommandParameter("mode", CommandParamType.INT, false),
                                new CommandParameter("player", CommandParamType.TARGET, true)
                        }, new CommandParameter[]{
                                new CommandParameter("mode", new String[]{"survival", "s", "creative", "c",
                                        "adventure", "a", "spectator", "spc", "view", "v"}),
                                new CommandParameter("player", CommandParamType.TARGET, true)
                        })
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            return false;
        }

        int gameMode = Server.getGamemodeFromString(args[0]);
        if (gameMode == -1) {
            sender.sendMessage("Unknown game mode");
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
            return false;
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
                CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.gamemode.success.self", Server.getGamemodeString(gameMode)));
            } else {
                target.sendMessage(new TranslationContainer("gameMode.changed"));
                CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.gamemode.success.other", target.getName(), Server.getGamemodeString(gameMode)));
            }
        }

        return true;
    }
}

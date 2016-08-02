package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class GamemodeCommand extends VanillaCommand {

    public GamemodeCommand(String name) {
        super(name, "%nukkit.command.gamemode.description", "%commands.gamemode.usage");
        this.setPermission("nukkit.command.gamemode");
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

        int gameMode = Server.getGamemodeFromString(args[0]);

        if (gameMode == -1) {
            sender.sendMessage("Unknown game mode");

            return true;
        }

        CommandSender target = sender;

        if (args.length > 1) {
            target = sender.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));

                return true;
            }
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return true;
        }

        ((Player) target).setGamemode(gameMode);

        if (gameMode != ((Player) target).getGamemode()) {
            sender.sendMessage("Game mode update for " + target.getName() + " failed");
        } else {
            if (target.equals(sender)) {
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(gameMode)));
            } else {
                target.sendMessage(new TranslationContainer("gameMode.changed"));
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.gamemode.success.other", new String[]{target.getName(), Server.getGamemodeString(gameMode)}));
            }
        }

        return true;
    }
}

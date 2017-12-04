package cn.nukkit.server.command.defaults;

import cn.nukkit.server.IPlayer;
import cn.nukkit.server.Player;
import cn.nukkit.server.command.Command;
import cn.nukkit.server.command.CommandSender;
import cn.nukkit.server.command.data.CommandParameter;
import cn.nukkit.server.lang.TranslationContainer;
import cn.nukkit.server.utils.TextFormat;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class DeopCommand extends VanillaCommand {
    public DeopCommand(String name) {
        super(name, "%nukkit.command.deop.description", "%commands.deop.description");
        this.setPermission("nukkit.command.op.take");
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParameter.ARG_TYPE_TARGET, false)
        });
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

        String playerName = args[0];
        IPlayer player = sender.getServer().getOfflinePlayer(playerName);
        player.setOp(false);

        if (player instanceof Player) {
            ((Player) player).sendMessage(new TranslationContainer(TextFormat.GRAY + "%commands.deop.message"));
        }

        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.deop.success", new String[]{player.getName()}));

        return true;
    }
}

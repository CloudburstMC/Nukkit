package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.IPlayer;
import cn.nukkit.server.Player;
import cn.nukkit.server.command.Command;
import cn.nukkit.server.command.data.CommandParameter;
import cn.nukkit.server.util.TextFormat;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class OpCommand extends VanillaCommand {

    public OpCommand(String name) {
        super(name, "%nukkit.command.op.description", "%commands.op.description");
        this.setPermission("nukkit.command.op.give");
        this.commandParameters.clear();
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
            sender.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
            return false;
        }

        String name = args[0];
        IPlayer player = sender.getServer().getOfflinePlayer(name);

        Command.broadcastCommandMessage(sender, new TranslatedMessage("commands.op.success", player.getName()));
        if (player instanceof Player) {
            ((Player) player).sendMessage(new TranslatedMessage(TextFormat.GRAY + "%commands.op.message"));
        }

        player.setOp(true);

        return true;
    }
}

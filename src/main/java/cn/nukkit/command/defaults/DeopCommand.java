package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.IPlayer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class DeopCommand extends Command {
    public DeopCommand() {
        super("deop", CommandData.builder("deop")
                .setDescription("commands.deop.description")
                .setUsageMessage("/deop <player>")
                .setPermissions("nukkit.command.op.take")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("player", CommandParamType.TARGET, false)
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

        String playerName = args[0];
        IPlayer player = sender.getServer().getOfflinePlayer(playerName);
        player.setOp(false);

        if (player instanceof Player) {
            ((Player) player).sendMessage(new TranslationContainer(TextFormat.GRAY + "%commands.deop.message"));
        }

        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.deop.success", player.getName()));

        return true;
    }
}

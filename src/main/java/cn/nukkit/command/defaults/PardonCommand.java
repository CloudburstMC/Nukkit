package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PardonCommand extends Command {

    public PardonCommand() {
        super("pardon", CommandData.builder("pardon")
                .setUsageMessage("/unban <player>")
                .setPermissions("nukkit.command.unban.player")
                .setAliases("unban")
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

        if (args.length != 1) {
            return false;
        }

        sender.getServer().getNameBans().remove(args[0]);

        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.unban.success", args[0]));

        return true;
    }
}

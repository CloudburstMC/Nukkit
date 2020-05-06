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

import java.util.Optional;
import java.util.UUID;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class OpCommand extends Command {

    public OpCommand() {
        super("op", CommandData.builder("op")
                .setDescription("commands.op.description")
                .setUsageMessage("/op <player>")
                .setPermissions("nukkit.command.op.give")
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

        String name = args[0];
        Optional<UUID> uuid = sender.getServer().lookupName(name);

        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.op.success", name));
        if (uuid.isPresent()) {
            IPlayer player = sender.getServer().getOfflinePlayer(uuid.get());
            if (player instanceof Player) {
                ((Player) player).sendMessage(new TranslationContainer(TextFormat.GRAY + "%commands.op.message"));
            }
            player.setOp(true);
        } else {
            sender.getServer().addOp(name);
        }

        return true;
    }
}

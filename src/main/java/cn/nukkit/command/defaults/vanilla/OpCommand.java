package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.IPlayer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.UUID;

import static cn.nukkit.command.args.builder.RequiredArgumentBuilder.requiredArg;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
@Log4j2
public class OpCommand extends VanillaCommand {

    public OpCommand(String name) {
        super(name, "commands.op.description", "/op <player>");
        this.setPermission("nukkit.command.op.give");

        registerOverload().then(requiredArg("player", CommandParamType.TARGET));
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        String name = args[0];
        Optional<UUID> uuid = sender.getServer().lookupName(name);

        if (uuid.isPresent()) {
            IPlayer player = sender.getServer().getOfflinePlayer(uuid.get());

            if(player.isOp()) {
                sender.sendMessage(new TranslationContainer("commands.op.failed", name));
                return true;
            }

            if (player instanceof Player) {
                ((Player) player).sendMessage(new TranslationContainer(TextFormat.GRAY + "%commands.op.message"));
            }
            player.setOp(true);
        } else {
            sender.getServer().addOp(name);
        }

        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.op.success", name));
        return true;
    }
}

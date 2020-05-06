package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;

import static cn.nukkit.command.args.builder.RequiredArgumentBuilder.requiredArg;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PardonCommand extends VanillaCommand {

    public PardonCommand(String name) {
        super(name);
        this.setPermission("nukkit.command.unban.player");
        this.setAliases(new String[]{"unban"});
        this.setUsage("/unban <player>");

        registerOverload().then(requiredArg("player", CommandParamType.TARGET));
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        sender.getServer().getNameBans().remove(args[0]);

        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.unban.success", args[0]));

        return true;
    }
}

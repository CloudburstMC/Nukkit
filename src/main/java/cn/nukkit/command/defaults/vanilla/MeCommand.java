package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

import java.util.Arrays;

import static cn.nukkit.command.args.builder.RequiredArgumentBuilder.requiredArg;

/**
 * Created on 2015/11/12 by xtypr.
 * @author lukeeey
 * @author xtypr
 */
public class MeCommand extends VanillaCommand {

    public MeCommand(String name) {
        super(name, "commands.me.description", "/me <action>");
        this.setPermission("nukkit.command.me");

        registerOverload().then(requiredArg("action...", CommandParamType.RAWTEXT));
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
        String name = sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName();
        String message = String.join(" ", args);

        sender.getServer().broadcastMessage(new TranslationContainer("chat.type.emote", name, TextFormat.WHITE + message));
        return true;
    }
}

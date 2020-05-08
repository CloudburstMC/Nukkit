package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

import java.util.Arrays;
import java.util.Objects;

import static cn.nukkit.command.args.builder.RequiredArgumentBuilder.requiredArg;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class TellCommand extends VanillaCommand {

    public TellCommand(String name) {
        super(name, "commands.tell.description", "/tell <player> <message>", new String[]{"w", "msg"});
        this.setPermission("nukkit.command.tell");

        registerOverload()
                .then(requiredArg("player", CommandParamType.TARGET))
                .then(requiredArg("message", CommandParamType.RAWTEXT));
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        Player player = sender.getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.player.notFound"));
            return true;
        }
        if (Objects.equals(player, sender)) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.message.sameTarget"));
            return true;
        }

        String message = String.join(", ", Arrays.copyOfRange(args, 1, args.length));
        String displayName = (sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName());

        sender.sendMessage(new TranslationContainer("commands.message.display.outgoing", player.getDisplayName(), message));
        player.sendMessage(new TranslationContainer("commands.message.display.incoming", displayName, message));
        return true;
    }
}

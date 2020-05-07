package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

import static cn.nukkit.command.args.builder.LiteralArgumentBuilder.literal;
import static cn.nukkit.command.args.builder.LiteralsArgumentBuilder.literals;
import static cn.nukkit.command.args.builder.RequiredArgumentBuilder.requiredArg;

/**
 * @author Tee7even
 */
public class TitleCommand extends VanillaCommand {
    public TitleCommand(String name) {
        super(name, "commands.title.description", "/title <player> <clear|reset>\n/title <player> <|title|subtitle|actionbar> <text>\n/title <player> <times> <fadein> <stay> <fadeOut>");
        this.setPermission("nukkit.command.title");

        registerOverload()
                .then(requiredArg("player", CommandParamType.TARGET))
                .then(literal("clear"));

        registerOverload()
                .then(requiredArg("player", CommandParamType.TARGET))
                .then(literal("reset"));

        registerOverload()
                .then(requiredArg("player", CommandParamType.TARGET))
                .then(literals("titleLocation", "TitleType", new String[]{"title", "subtitle", "actionbar"}).expandEnum())
                .then(requiredArg("titleText", CommandParamType.STRING));


        registerOverload()
                .then(requiredArg("player", CommandParamType.TARGET))
                .then(literal("times"))
                .then(requiredArg("fadeIn", CommandParamType.INT))
                .then(requiredArg("stay", CommandParamType.INT))
                .then(requiredArg("fadeOut", CommandParamType.INT));
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

        Player player = Server.getInstance().getPlayerExact(args[0]);
        if (player == null) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return true;
        }

        if (args.length == 2) {
            switch (args[1].toLowerCase()) {
                case "clear":
                    player.clearTitle();
                    sender.sendMessage(new TranslationContainer("nukkit.command.title.clear", player.getName()));
                    break;
                case "reset":
                    player.resetTitleSettings();
                    sender.sendMessage(new TranslationContainer("nukkit.command.title.reset", player.getName()));
                    break;
                default:
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return false;
            }
        } else if (args.length == 3) {
            switch (args[1].toLowerCase()) {
                case "title":
                    player.sendTitle(args[2]);
                    sender.sendMessage(new TranslationContainer("nukkit.command.title.title",
                            TextFormat.clean(args[2]), player.getName()));
                    break;
                case "subtitle":
                    player.setSubtitle(args[2]);
                    sender.sendMessage(new TranslationContainer("nukkit.command.title.subtitle", TextFormat.clean(args[2]), player.getName()));
                    break;
                /*case "actionbar":
                    player.sendActionBarTitle(args[2]);
                    sender.sendMessage(new TranslationContainer("nukkit.command.title.actionbar", new String[]{TextFormat.clean(args[2]), player.getName()}));
                    break;*/
                default:
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return false;
            }
        } else if (args.length == 5) {
            if (args[1].toLowerCase().equals("times")) {
                try {
                    /*player.setTitleAnimationTimes(Integer.valueOf(args[2]), //fadeIn
                            Integer.valueOf(args[3]), //stay
                            Integer.valueOf(args[4])); //fadeOut*/
                    sender.sendMessage(new TranslationContainer("nukkit.command.title.times.success",
                            args[2], args[3], args[4], player.getName()));
                } catch (NumberFormatException exception) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.exception"));
                }
            } else {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return false;
            }
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        return true;
    }
}

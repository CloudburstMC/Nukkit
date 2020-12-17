package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

/**
 * @author Tee7even
 */
public class TitleCommand extends VanillaCommand {
    public TitleCommand(String name) {
        super(name, "%nukkit.command.title.description", "%nukkit.command.title.usage");
        this.setPermission("nukkit.command.title");

        this.commandParameters.clear();
        this.commandParameters.put("clear", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("clear", new CommandEnum("TitleClear", "clear"))
        });
        this.commandParameters.put("reset", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("reset", new CommandEnum("TitleReset", "reset"))
        });
        this.commandParameters.put("set", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("titleLocation", new CommandEnum("TitleSet", "title", "subtitle", "actionbar")),
                CommandParameter.newType("titleText", CommandParamType.MESSAGE)
        });
        this.commandParameters.put("times", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("times", new CommandEnum("TitleTimes", "times")),
                CommandParameter.newType("fadeIn", CommandParamType.INT),
                CommandParameter.newType("stay", CommandParamType.INT),
                CommandParameter.newType("fadeOut", CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
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
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%nukkit.command.title.times.fail"));
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

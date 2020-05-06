package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.level.Level;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class TimeCommand extends VanillaCommand {

    public TimeCommand(String name) {
        super(name, "commands.time.description", "/time <add|set|start|stop> [value]");
        this.setPermission("nukkit.command.time.add;" +
                "nukkit.command.time.set;" +
                "nukkit.command.time.start;" +
                "nukkit.command.time.stop");

        registerOverload()
                .literal("add")
                .requiredArg("amount", CommandParamType.INT);

        registerOverload()
                .literal("set")
                .requiredArg("amount", CommandParamType.INT);

        registerOverload()
                .literal("set")
                .requiredArg("time", "TimeSpec", new String[]{"day", "midnight", "night", "noon", "sunrise", "sunset"});

        registerOverload()
                .literal("query"); // TODO: day, daytime, gametime
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        if ("query".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.query")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));

                return true;
            }
            Level level;
            if (sender instanceof Player) {
                level = ((Player) sender).getLevel();
            } else {
                level = sender.getServer().getDefaultLevel();
            }
            sender.sendMessage(new TranslationContainer("commands.time.query.gametime", level.getTime()));
            return true;
        }


        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        if ("set".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.set")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));

                return true;
            }

            int value;
            if ("day".equals(args[1])) {
                value = Level.TIME_DAY;
            } else if ("night".equals(args[1])) {
                value = Level.TIME_NIGHT;
            } else if ("midnight".equals(args[1])) {
                value = Level.TIME_MIDNIGHT;
            } else if ("noon".equals(args[1])) {
                value = Level.TIME_NOON;
            } else if ("sunrise".equals(args[1])) {
                value = Level.TIME_SUNRISE;
            } else if ("sunset".equals(args[1])) {
                value = Level.TIME_SUNSET;
            } else {
                try {
                    value = Math.max(0, Integer.parseInt(args[1]));
                } catch (Exception e) {
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return true;
                }
            }

            for (Level level : sender.getServer().getLevels()) {
                level.checkTime();
                level.setTime(value);
                level.checkTime();
            }
            CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.time.set", value));
        } else if ("add".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.add")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));

                return true;
            }

            int value;
            try {
                value = Math.max(0, Integer.parseInt(args[1]));
            } catch (Exception e) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return true;
            }

            for (Level level : sender.getServer().getLevels()) {
                level.checkTime();
                level.setTime(level.getTime() + value);
                level.checkTime();
            }
            CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.time.added", value));
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        }

        return true;
    }
}

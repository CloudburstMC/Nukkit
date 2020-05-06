package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class TimeCommand extends Command {

    public TimeCommand() {
        super("time", CommandData.builder("time")
                .setDescription("commands.time.description")
                .setUsageMessage("/time <add|set|start|stop> [value]")
                .setPermissions("nukkit.command.time.add",
                        "nukkit.command.time.set",
                        "nukkit.command.time.start",
                        "nukkit.command.time.stop")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("start|stop", CommandParamType.STRING, false)
                }, new CommandParameter[]{
                        new CommandParameter("add|set", CommandParamType.STRING, false),
                        new CommandParameter("value", CommandParamType.INT, false)
                }, new CommandParameter[]{
                        new CommandParameter("add|set", CommandParamType.STRING, false),
                        new CommandParameter("value", CommandParamType.STRING, false)
                })
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 1) {
            return false;
        }

        if ("start".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.start")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));

                return true;
            }
            for (Level level : sender.getServer().getLevels()) {
                level.checkTime();
                level.startTime();
                level.checkTime();
            }
            CommandUtils.broadcastCommandMessage(sender, "Restarted the time");
            return true;
        } else if ("stop".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.stop")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));

                return true;
            }
            for (Level level : sender.getServer().getLevels()) {
                level.checkTime();
                level.stopTime();
                level.checkTime();
                CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.time.stop", level.getTime()));
            }
            return true;
        } else if ("query".equals(args[0])) {
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
                    return false;
                }
            }

            for (Level level : sender.getServer().getLevels()) {
                level.checkTime();
                level.setTime(value);
                level.checkTime();
            }
            CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.time.set", value));
        } else if ("add".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.add")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));

                return true;
            }

            int value;
            try {
                value = Math.max(0, Integer.parseInt(args[1]));
            } catch (Exception e) {
                return false;
            }

            for (Level level : sender.getServer().getLevels()) {
                level.checkTime();
                level.setTime(level.getTime() + value);
                level.checkTime();
            }
            CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.time.added", value));
        } else {
            return false;
        }

        return true;
    }
}

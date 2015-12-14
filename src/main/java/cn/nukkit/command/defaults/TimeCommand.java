package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class TimeCommand extends VanillaCommand {

    public TimeCommand(String name) {
        super(name, "%nukkit.command.time.description", "%nukkit.command.time.usage");
        this.setPermission("nukkit.command.time.add;" +
                "nukkit.command.time.set;" +
                "nukkit.command.time.start;" +
                "nukkit.command.time.stop");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        if ("start".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.start")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));

                return true;
            }
            for (Level level : sender.getServer().getLevels().values()) {
                level.checkTime();
                level.startTime();
                level.checkTime();
            }
            Command.broadcastCommandMessage(sender, "Restarted the time");
            return true;
        } else if ("stop".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.stop")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));

                return true;
            }
            for (Level level : sender.getServer().getLevels().values()) {
                level.checkTime();
                level.stopTime();
                level.checkTime();
            }
            Command.broadcastCommandMessage(sender, "Stopped the time");
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
            sender.sendMessage(new TranslationContainer("commands.time.query", String.valueOf(level.getTime())));
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
                value = 0;
            } else if ("night".equals(args[1])) {
                value = Level.TIME_NIGHT;
            } else if (args[1].matches("^[1-9]+\\d*$")){
                value = Math.max(0, Integer.parseInt(args[1]));
            } else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.usage"));
                return true;
            }

            for (Level level : sender.getServer().getLevels().values()) {
                level.checkTime();
                level.setTime(value);
                level.checkTime();
            }
            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.time.set", String.valueOf(value)));
        } else if ("add".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.add")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));

                return true;
            }

            int value = Math.max(0, Integer.parseInt(args[1]));
            for (Level level : sender.getServer().getLevels().values()) {
                level.checkTime();
                level.setTime(level.getTime() + value);
                level.checkTime();
            }
            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.time.added", String.valueOf(value)));
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        }

        return true;
    }
}

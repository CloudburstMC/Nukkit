package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class WeatherCommand extends VanillaCommand {

    public WeatherCommand(String name) {
        super(name, "%nukkit.command.weather.description", "%commands.weather.usage");
        this.setPermission("nukkit.command.weather");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("type", new CommandEnum("WeatherType", "clear", "rain", "thunder")),
                CommandParameter.newType("duration", true, CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0 || args.length > 2) {
            sender.sendMessage(new TranslationContainer("commands.weather.usage", this.usageMessage));
            return false;
        }

        String weather = args[0];
        Level level;
        int seconds;
        if (args.length > 1) {
            try {
                seconds = Integer.parseInt(args[1]);
            } catch (Exception e) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return true;
            }
        } else {
            seconds = 600 * 20;
        }

        if (sender instanceof Player) {
            level = ((Player) sender).getLevel();
        } else {
            level = sender.getServer().getDefaultLevel();
        }

        switch (weather) {
            case "clear":
                level.setRaining(false);
                level.setThundering(false);
                level.setRainTime(seconds * 20);
                level.setThunderTime(seconds * 20);
                Command.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.weather.clear"));
                return true;
            case "rain":
                level.setRaining(true);
                level.setRainTime(seconds * 20);
                Command.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.weather.rain"));
                return true;
            case "thunder":
                level.setThundering(true);
                level.setRainTime(seconds * 20);
                level.setThunderTime(seconds * 20);
                Command.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.weather.thunder"));
                return true;
            default:
                sender.sendMessage(new TranslationContainer("commands.weather.usage", this.usageMessage));
                return false;
        }

    }
}

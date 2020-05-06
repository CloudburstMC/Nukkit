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

/**
 * author: Angelic47
 * Nukkit Project
 */
public class WeatherCommand extends Command {

    public WeatherCommand() {
        super("weather", CommandData.builder("weather")
                .setDescription("commands.weather.description")
                .setUsageMessage("/weather <clear|rain|thunder> [time]")
                .setPermissions("nukkit.command.weather")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("clear|rain|thunder", CommandParamType.STRING, false),
                        new CommandParameter("duration in seconds", CommandParamType.INT, true)
                }).build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0 || args.length > 2) {
            return false;
        }

        String weather = args[0];
        Level level;
        int seconds;
        if (args.length > 1) {
            try {
                seconds = Integer.parseInt(args[1]);
            } catch (Exception e) {
                return false;
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
                CommandUtils.broadcastCommandMessage(sender,
                        new TranslationContainer("%commands.weather.clear"));
                return true;
            case "rain":
                level.setRaining(true);
                level.setRainTime(seconds * 20);
                CommandUtils.broadcastCommandMessage(sender,
                        new TranslationContainer("%commands.weather.rain"));
                return true;
            case "thunder":
                level.setThundering(true);
                level.setRainTime(seconds * 20);
                level.setThunderTime(seconds * 20);
                CommandUtils.broadcastCommandMessage(sender,
                        new TranslationContainer("%commands.weather.thunder"));
                return true;
            default:
                return false;
        }

    }
}

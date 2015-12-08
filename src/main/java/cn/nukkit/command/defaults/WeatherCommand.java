package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class WeatherCommand extends VanillaCommand {

    private java.util.Random rand = new java.util.Random();

    public WeatherCommand(String name) {
        super(name, "Sets the weather in currect world", "%commands.weather.usage");
        this.setPermission("nukkit.command.weather");
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
        String time;
        Level level;
        int seconds;
        if(args.length > 1) {
            time = args[1];
            seconds = Integer.parseInt(time);
        }
        else {
            seconds = 0;
        }

        if(sender instanceof Player) {
            level = ((Player) sender).getLevel();
        }
        else {
            level = sender.getServer().getDefaultLevel();
        }

        switch (weather) {
            case "clear":
                level.setStorm(false);
                level.setThundering(false);
                if(seconds > 0) {
                    level.setWeatherDuration(seconds);
                    level.setThunderDuration(seconds + rand.nextInt(168000));
                }
                else {
                    level.setWeatherDuration(rand.nextInt(168000) + 12000);
                    level.setThunderDuration(rand.nextInt(168000) + 12000);
                }
                Command.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.weather.clear"));
                return true;
            case "rain":
                level.setStorm(true);
                level.setThundering(false);
                if(seconds > 0) {
                    level.setWeatherDuration(seconds);
                    level.setThunderDuration(rand.nextInt(168000) + seconds);
                }
                else {
                    level.setWeatherDuration(rand.nextInt(12000) + 12000);
                    level.setThunderDuration(rand.nextInt(168000) + 12000);
                }
                Command.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.weather.rain"));
                return true;
            case "thunder":
                level.setStorm(true);
                level.setThundering(false);
                if(seconds > 0) {
                    level.setWeatherDuration(seconds);
                    level.setThunderDuration(seconds);
                }
                else {
                    level.setWeatherDuration(rand.nextInt(12000) + 12000);
                    level.setThunderDuration(rand.nextInt(12000) + 12000);
                }
                Command.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.weather.clear"));
                return true;
            default:
                sender.sendMessage(new TranslationContainer("commands.weather.usage", this.usageMessage));
                return false;
        }

    }
}

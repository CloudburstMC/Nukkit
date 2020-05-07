package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.level.Level;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import lombok.extern.log4j.Log4j2;

import static cn.nukkit.command.args.builder.LiteralsArgumentBuilder.literals;
import static cn.nukkit.command.args.builder.OptionalArgumentBuilder.optionalArg;

/**
 * author: Angelic47
 * Nukkit Project
 */
@Log4j2
public class WeatherCommand extends VanillaCommand {

    public WeatherCommand(String name) {
        super(name, "commands.weather.description", "/weather <clear|rain|thunder> <time>");
        this.setPermission("nukkit.command.weather");

        registerOverload()
                .then(literals("type", "WeatherType", new String[]{"clear", "rain", "thunder"}))
                .then(optionalArg("duration", CommandParamType.INT));
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
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
                CommandUtils.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.weather.clear"));
                return true;
            case "rain":
                level.setRaining(true);
                level.setRainTime(seconds * 20);
                CommandUtils.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.weather.rain"));
                return true;
            case "thunder":
                level.setThundering(true);
                level.setRainTime(seconds * 20);
                level.setThunderTime(seconds * 20);
                CommandUtils.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.weather.thunder"));
                return true;
            default:
                sender.sendMessage(new TranslationContainer("commands.weather.usage", this.usageMessage));
                return false;
        }

    }
}

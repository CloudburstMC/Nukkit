package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class WeatherCommand extends BaseCommand {

    public WeatherCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("weather", "commands.weather.description");

        dispatcher.register(literal("weather")
                .requires(requirePermission("nukkit.command.weather"))
                .then(literal("clear")
                        .executes(this::clear)
                            .then(argument("duration", integer()).executes(this::clear)))
                .then(literal("rain")
                        .executes(this::rain)
                            .then(argument("duration", integer()).executes(this::clear)))
                .then(literal("thunder")
                        .executes(this::thunder)
                            .then(argument("duration", integer()).executes(this::clear))));
    }

    public int clear(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        int duration = 600 * 20;

        // TODO: accept duration
        //getInteger(context, "duration") * 20;
        Level level = (source instanceof Player) ? ((Player) source).getLevel() : source.getServer().getDefaultLevel();

        level.setRaining(false);
        level.setThundering(false);
        level.setRainTime(duration * 20);
        level.setThunderTime(duration * 20);

        sendAdminMessage(source, new TranslationContainer("commands.weather.clear"));
        return duration;
    }

    public int rain(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        int duration = 600 * 20;

        // TODO: accept duration
        //getInteger(context, "duration") * 20;
        Level level = (source instanceof Player) ? ((Player) source).getLevel() : source.getServer().getDefaultLevel();

        level.setRaining(true);
        level.setRainTime(duration * 20);
        level.setThunderTime(duration * 20);

        sendAdminMessage(source, new TranslationContainer("commands.weather.rain"));
        return duration;
    }

    public int thunder(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        int duration = 600 * 20;

        // TODO: accept duration
        //getInteger(context, "duration") * 20;
        Level level = (source instanceof Player) ? ((Player) source).getLevel() : source.getServer().getDefaultLevel();

        level.setThundering(true);
        level.setRainTime(duration * 20);
        level.setThunderTime(duration * 20);

        sendAdminMessage(source, new TranslationContainer("commands.weather.thunder"));
        return duration;
    }
}

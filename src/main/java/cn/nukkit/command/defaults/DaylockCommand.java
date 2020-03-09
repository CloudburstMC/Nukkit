package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.gamerule.GameRule;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.player.Player;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import java.util.ArrayList;

import static cn.nukkit.command.args.DifficultyArgument.difficulty;
import static cn.nukkit.command.args.DifficultyArgument.getDifficulty;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;

public class DaylockCommand extends BaseCommand {

    public DaylockCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("daylock", "commands.daylock.description"); // TODO: aliases (alwaysday)
        // TODO: lang
        dispatcher.register(literal("daylock")
                .requires(requirePermission("nukkit.command.daylock"))
                .then(argument("lock", bool()).executes(this::run))
                .executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Level level = source instanceof Player ? ((Player) source).getLevel() : Server.getInstance().getDefaultLevel();
        boolean lock = true;

        try {
            lock = getBool(context, "lock");
        } catch (IllegalArgumentException ex) {
            // Do nothing
        }

        level.getGameRules().put(GameRules.DO_DAYLIGHT_CYCLE, lock);

        if(lock) {
            sendAdminMessage(source, new TranslationContainer("commands.always.day.locked"));
        } else {
            sendAdminMessage(source, new TranslationContainer("commands.always.day.unlocked"));
        }
        return 1;
    }
}

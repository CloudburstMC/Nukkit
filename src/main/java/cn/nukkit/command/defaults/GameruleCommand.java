package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.gamerule.GameRule;
import cn.nukkit.level.gamerule.GameRuleMap;
import cn.nukkit.player.Player;
import cn.nukkit.registry.GameRuleRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import java.util.Arrays;
import java.util.StringJoiner;

import static cn.nukkit.command.args.GameRuleArgument.gamerule;
import static cn.nukkit.command.args.GameRuleArgument.getGameRule;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class GameruleCommand extends BaseCommand {
    private static final GameRuleRegistry registry = GameRuleRegistry.get();

    public GameruleCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("gamerule", "commands.gamerule.description");

        dispatcher.register(literal("gamerule")
                .requires(requirePermission("nukkit.command.gamerule"))
                .then(argument("rule", gamerule()).executes(this::queryRule)
                        .then(argument("value", string()).executes(this::setRule)))
                .executes(this::queryAll));
    }

    public int queryAll(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Level level = source instanceof Player ? ((Player) source).getLevel() : source.getServer().getDefaultLevel();
        StringJoiner rulesJoiner = new StringJoiner(", ");

        for (GameRule rule : registry.getRules()) {
            rulesJoiner.add(rule.getName().toLowerCase() + " = " + level.getGameRules().get(rule).toString());
        }

        source.sendMessage(rulesJoiner.toString());
        return 1;
    }

    public int queryRule(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Level level = source instanceof Player ? ((Player) source).getLevel() : source.getServer().getDefaultLevel();
        GameRule rule = getGameRule(context, "rule", level);

        source.sendMessage(rule.getName() + " = " + level.getGameRules().get(rule).toString());
        return 1;
    }

    public int setRule(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Level level = source instanceof Player ? ((Player) source).getLevel() : source.getServer().getDefaultLevel();
        GameRule rule = getGameRule(context, "rule", level);
        String value = getString(context, "value");

        level.getGameRules().put(rule, rule.parse(value));

        source.sendMessage(new TranslationContainer("commands.gamerule.success", rule.getName(), value));
        return 1;
    }
}

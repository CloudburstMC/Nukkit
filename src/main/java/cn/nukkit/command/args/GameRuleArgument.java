package cn.nukkit.command.args;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSource;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.gamerule.GameRule;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.player.Player;
import cn.nukkit.potion.Effect;
import cn.nukkit.registry.GameRuleRegistry;
import cn.nukkit.registry.RegistryException;
import cn.nukkit.utils.Identifier;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class GameRuleArgument implements ArgumentType<GameRule> {
    public static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(rule ->
            new LiteralMessage("Game rule not found: " + rule));

    private static final GameRuleRegistry registry = GameRuleRegistry.get();

    private Level level;

    public static GameRuleArgument gamerule() {
        return new GameRuleArgument();
    }

    public static GameRule getGameRule(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, GameRule.class);
    }

    public static GameRule getGameRule(CommandContext<CommandSource> context, String name, Level level) {
        // TODO: parse
        return context.getArgument(name, GameRule.class);
    }

    @Override
    public GameRule parse(StringReader reader) throws CommandSyntaxException {
        String ruleNameString = reader.readUnquotedString();
        GameRule rule = registry.fromString(ruleNameString);

        // TODO: support for other levels
        Level level = Server.getInstance().getDefaultLevel();

        if (rule == null || !level.getGameRules().contains(rule)) {
            throw NOT_FOUND.create(ruleNameString);
        }

        return rule;
    }
}

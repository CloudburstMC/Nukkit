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

import java.util.Arrays;
import java.util.List;

public class DifficultyArgument implements ArgumentType<String> {
    public static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(difficulty ->
            new LiteralMessage("Unknown difficulty: " + difficulty));

    private final String[] DIFFICULTY_VALUES = new String[]{"peaceful", "p", "easy", "e", "normal", "n", "hard", "h"};

    public static DifficultyArgument difficulty() {
        return new DifficultyArgument();
    }

    public static String getDifficulty(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String difficultyString = reader.readUnquotedString();
        List<String> list = Arrays.asList(DIFFICULTY_VALUES);

        if(!list.contains(difficultyString)) {
            throw NOT_FOUND.create(difficultyString);
        }

        return difficultyString;
    }
}

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

public class GameModeArgument implements ArgumentType<String> {
    public static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(gamemode ->
            new LiteralMessage("Unknown gamemode: " + gamemode));

    private final String[] GAMEMODE_VALUES = new String[]{"survival", "s", "creative", "c", "adventure", "a", "spectator", "sp"};

    public static GameModeArgument gamemode() {
        return new GameModeArgument();
    }

    public static String getGamemode(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String gamemodeString = reader.readUnquotedString();
        List<String> list = Arrays.asList(GAMEMODE_VALUES);

        if(!list.contains(gamemodeString)) {
            throw NOT_FOUND.create(gamemodeString);
        }

        return gamemodeString;
    }
}

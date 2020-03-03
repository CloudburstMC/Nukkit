package cn.nukkit.command.args;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSource;
import cn.nukkit.level.Position;
import cn.nukkit.player.Player;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Identifier;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class PositionArgument implements ArgumentType<Position> {

    public static PositionArgument effect() {
        return new PositionArgument();
    }

    public static Position getPosition(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, Position.class);
    }

    @Override
    public Position parse(StringReader reader) throws CommandSyntaxException {
        // TODO
        return null;
    }
}

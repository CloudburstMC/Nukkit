package cn.nukkit.command.args;

import cn.nukkit.command.CommandSource;
import cn.nukkit.level.BlockPosition;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class PositionArgument implements ArgumentType<BlockPosition> {

    public static PositionArgument effect() {
        return new PositionArgument();
    }

    public static BlockPosition getPosition(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, BlockPosition.class);
    }

    @Override
    public BlockPosition parse(StringReader reader) throws CommandSyntaxException {
        // TODO
        return null;
    }
}

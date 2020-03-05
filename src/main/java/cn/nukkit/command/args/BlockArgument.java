package cn.nukkit.command.args;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSource;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.player.Player;
import cn.nukkit.potion.Effect;
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

public class BlockArgument implements ArgumentType<Block> {
    public static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(blockName ->
            new LiteralMessage("Block not found: " + blockName));

    public static BlockArgument block() {
        return new BlockArgument();
    }

    public static Block getBlock(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, Block.class);
    }

    @Override
    public Block parse(StringReader reader) throws CommandSyntaxException {
        Identifier identifier = Identifier.from(reader);
        Block block = null;

        try {
            block = Block.get(identifier);
        } catch (RegistryException ex) {
            throw NOT_FOUND.create(identifier.getName());
        }

        return block;
    }
}

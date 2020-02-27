package cn.nukkit.command.args;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSource;
import cn.nukkit.player.Player;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerArgument implements ArgumentType<Player> {
    public static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(new LiteralMessage("Player not found"));

    public static PlayerArgument player() {
        return new PlayerArgument();
    }

    public static Player getPlayer(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, Player.class);
    }

    @Override
    public Player parse(StringReader reader) throws CommandSyntaxException {
        if(reader.canRead() && reader.peek() == '@') {
            // TODO: selectors
            return null;
        } else {
            int cursor = reader.getCursor();

            while(reader.canRead() && reader.peek() != ' ') {
                reader.skip();
            }

            String playerName = reader.getString().substring(cursor, reader.getCursor());
            Player player = Server.getInstance().getPlayer(playerName);

            if(player == null) {
                throw NOT_FOUND.create();
            } else {
                return player;
            }
        }
    }
}

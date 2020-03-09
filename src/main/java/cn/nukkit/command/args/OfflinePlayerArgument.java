package cn.nukkit.command.args;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSource;
import cn.nukkit.player.IPlayer;
import cn.nukkit.player.OfflinePlayer;
import cn.nukkit.player.Player;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.*;

public class OfflinePlayerArgument implements ArgumentType<IPlayer> {

    public static OfflinePlayerArgument offlinePlayer() {
        return new OfflinePlayerArgument();
    }

    public static IPlayer getOfflinePlayer(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, IPlayer.class);
    }

    @Override
    public IPlayer parse(StringReader reader) throws CommandSyntaxException {
        if(reader.canRead() && reader.peek() == '@') {
            // TODO: selectors
            return null;
        } else {
            int cursor = reader.getCursor();

            while(reader.canRead() && reader.peek() != ' ') {
                reader.skip();
            }

            String playerName = reader.getString().substring(cursor, reader.getCursor());
            Optional<UUID> uuid = Server.getInstance().lookupName(playerName);

            if(!uuid.isPresent()) {
                throw PlayerArgument.NOT_FOUND.create();
            }

            IPlayer player = Server.getInstance().getOfflinePlayer(uuid.get());

            if(!player.hasPlayedBefore()) {
                throw PlayerArgument.NOT_FOUND.create();
            } else {
                return player;
            }
        }
    }
}

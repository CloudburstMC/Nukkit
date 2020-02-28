package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import cn.nukkit.player.IPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Iterator;

import static cn.nukkit.command.args.OfflinePlayerArgument.getOfflinePlayer;
import static cn.nukkit.command.args.OfflinePlayerArgument.offlinePlayer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class BanListCommand extends BaseCommand {

    public BanListCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("banlist", "%nukkit.command.banlist.description");

        dispatcher.register(literal("banlist")
                .requires(requirePermission("nukkit.command.banlist"))
                .then(literal("ips").executes(context -> run(context, Type.IPS)))
                .then(literal("players").executes(context -> run(context, Type.PLAYERS)))
                .executes(context -> run(context, Type.PLAYERS)));
    }

    public int run(CommandContext<CommandSource> context, Type type) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        BanList banList;

        boolean ips = false;
        switch(type) {
            case IPS:
                banList = source.getServer().getIPBans();
                ips = true;
                break;
            case PLAYERS:
            default:
                banList = source.getServer().getNameBans();
                break;
        }

        StringBuilder builder = new StringBuilder();
        Iterator<BanEntry> itr = banList.getEntires().values().iterator();
        while (itr.hasNext()) {
            builder.append(itr.next().getName());
            if (itr.hasNext()) {
                builder.append(", ");
            }
        }

        if (ips) {
            source.sendMessage(new TranslationContainer("commands.banlist.ips", String.valueOf(banList.getEntires().size())));
        } else {
            source.sendMessage(new TranslationContainer("commands.banlist.players", String.valueOf(banList.getEntires().size())));
        }
        source.sendMessage(builder.toString());
        return 1;
    }

    private enum Type {
        IPS,
        PLAYERS
    }
}

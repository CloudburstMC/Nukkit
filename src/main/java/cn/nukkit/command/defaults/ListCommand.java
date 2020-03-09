package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSource;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class ListCommand extends BaseCommand {

    public ListCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("list", "%nukkit.command.list.description");

        dispatcher.register(literal("list")
                .requires(requirePermission("nukkit.command.list"))
                .executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        Collection<Player> onlineList = source.getServer().getOnlinePlayers().values();
        StringBuilder builder = new StringBuilder();
        AtomicInteger onlineCount = new AtomicInteger();

        // TODO: iterator?
        onlineList.forEach(player -> {
            if (player.isOnline() && (!(source instanceof Player) || ((Player) source).canSee(player))) {
                builder.append(player.getDisplayName());
                builder.append(", ");

                onlineCount.getAndIncrement();
            }
        });

        String online = builder.toString();
        if (online.length() > 0) {
            online = online.substring(0, online.length() - 2);
        }

        source.sendMessage(new TranslationContainer("commands.players.list", onlineCount.get(), source.getServer().getMaxPlayers()));
        source.sendMessage(online);
        return 1;
    }
}

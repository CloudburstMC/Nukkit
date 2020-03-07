package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.player.Player;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import static cn.nukkit.command.args.PlayerArgument.getPlayer;
import static cn.nukkit.command.args.PlayerArgument.player;

public class PardonCommand extends BaseCommand {

    public PardonCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("pardon", "%nukkit.command.unban.player.description"); // TODO: aliases (unban)

        dispatcher.register(literal("pardon")
                .requires(requirePermission("nukkit.command.unban.player"))
                .then(argument("player", player()).executes(this::run)));
    }

    public int run(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");

        source.getServer().getNameBans().remove(target.getName());

        sendAdminMessage(source, new TranslationContainer("%commands.unban.success", target.getName()));
        return 1;
    }
}

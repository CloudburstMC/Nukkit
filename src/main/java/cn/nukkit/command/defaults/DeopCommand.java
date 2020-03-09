package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.IPlayer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static cn.nukkit.command.args.OfflinePlayerArgument.getOfflinePlayer;
import static cn.nukkit.command.args.OfflinePlayerArgument.offlinePlayer;
import static cn.nukkit.command.args.PlayerArgument.getPlayer;
import static cn.nukkit.command.args.PlayerArgument.player;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class DeopCommand extends BaseCommand {

    public DeopCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("deop", "%nukkit.command.deop.description");

        dispatcher.register(literal("deop")
                .requires(requirePermission("nukkit.command.op.take"))
                .then(argument("player", offlinePlayer()).executes(this::run)));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        IPlayer target = getOfflinePlayer(context, "player");

        target.setOp(false);

        if (target instanceof Player) {
            ((Player) target).sendMessage(new TranslationContainer(TextFormat.GRAY + "%commands.deop.message"));
        }

        sendAdminMessage(source, new TranslationContainer("commands.deop.success", target.getName()));

        return 1;
    }
}

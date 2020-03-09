package cn.nukkit.command.defaults;

import cn.nukkit.command.*;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.IPlayer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import static cn.nukkit.command.args.OfflinePlayerArgument.getOfflinePlayer;
import static cn.nukkit.command.args.OfflinePlayerArgument.offlinePlayer;

public class OpCommand extends BaseCommand {

    public OpCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("op", "commands.op.description");

        dispatcher.register(literal("op")
                .requires(requirePermission("nukkit.command.op.give"))
                .then(argument("player", offlinePlayer()).executes(this::run)));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        IPlayer target = getOfflinePlayer(context, "player");

        // Matches vanilla behaviour
        if(target.isOp()) {
            source.sendMessage(new TranslationContainer("commands.op.failed", target.getName()));
            return 1;
        }

        target.setOp(true);

        sendAdminMessage(source, new TranslationContainer("commands.op.success", target.getName()));

        if (target instanceof Player) {
            ((Player) target).sendMessage(new TranslationContainer(TextFormat.GRAY + "%commands.op.message"));
        }
        return 1;
    }
}

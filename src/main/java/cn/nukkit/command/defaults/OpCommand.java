package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
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
    public static final DynamicCommandExceptionType ALREADY_OP = new DynamicCommandExceptionType(name ->
            new LiteralMessage("Could not op (already op or higher): " + name));

    public OpCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("op", "%nukkit.command.op.description");

        dispatcher.register(literal("op")
                .requires(requirePermission("nukkit.command.op.give"))
                .then(argument("player", offlinePlayer()).executes(this::run)));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        IPlayer target = getOfflinePlayer(context, "player");

        // Matches vanilla behaviour
        if(target.isOp()) {
            throw ALREADY_OP.create(target.getName());
        }

        target.setOp(true);

        sendAdminMessage(source, new TranslationContainer("commands.op.success", target.getName()));

        if (target instanceof Player) {
            ((Player) target).sendMessage(new TranslationContainer(TextFormat.GRAY + "%commands.op.message"));
        }
        return 1;
    }
}

package cn.nukkit.command.defaults;

import cn.nukkit.command.*;
import cn.nukkit.command.args.CommandArgument;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Map;
import java.util.TreeMap;

import static cn.nukkit.command.args.CommandArgument.getCommand;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class HelpCommand extends BaseCommand {
    private CommandDispatcher<CommandSource> dispatcher;

    public HelpCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("help", "%nukkit.command.help.description");

        dispatcher.register(literal("help")
                .requires(requirePermission("nukkit.command.help"))
                .then(argument("page", integer()).executes(ctx -> run(ctx, getInteger(ctx, "page"))))
                .then(argument("command", CommandArgument.command()).executes(this::command))
                .executes(ctx -> run(ctx, 1)));

        this.dispatcher = dispatcher;
    }

    public int run(CommandContext<CommandSource> context, int page) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        int pageNumber = page;
        int pageHeight = 5;
            try {
                if (pageNumber <= 0) {
                    pageNumber = 1;
                }


            } catch (NumberFormatException e) {
                pageNumber = 1;

            }

        // TODO
        if (source instanceof ConsoleCommandSource) {
            pageHeight = Integer.MAX_VALUE;
        }

            Map<String, BaseCommand> commands = new TreeMap<>();
            for (BaseCommand cmd : source.getServer().getCommandDispatcher().getCommands().values()) {
                if (cmd.testPermissionSilent(source)) {
                    commands.put(cmd.getName(), cmd);
                }
            }
            int totalPage = commands.size() % pageHeight == 0 ? commands.size() / pageHeight : commands.size() / pageHeight + 1;
            pageNumber = Math.min(pageNumber, totalPage);
            if (pageNumber < 1) {
                pageNumber = 1;
            }

            source.sendMessage(new TranslationContainer("commands.help.header", String.valueOf(pageNumber), String.valueOf(totalPage)));
            int i = 1;
            for (BaseCommand command1 : commands.values()) {
                if (i >= (pageNumber - 1) * pageHeight + 1 && i <= Math.min(commands.size(), pageNumber * pageHeight)) {
                    source.sendMessage(TextFormat.DARK_GREEN + "/" + command1.getName() + ": " + TextFormat.WHITE + command1.getDescription());
                }
                i++;
            }

        return 1;
    }

    public int command(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        BaseCommand cmd = getCommand(context, "command");

        if (cmd != null) {
            if (cmd.testPermissionSilent(source)) {
                String message = TextFormat.YELLOW + "--------- " + TextFormat.WHITE + " Help: /" + cmd.getName() + TextFormat.YELLOW + " ---------\n";
                message += TextFormat.GOLD + "Description: " + TextFormat.WHITE + cmd.getDescription() + "\n";

                String usage = "";
                String[] usages = dispatcher.getAllUsage(dispatcher.getRoot().getChild(cmd.getName()), source, false);

                for(String u : usages) {
                    usage += "\n" + TextFormat.WHITE + "/" + cmd.getName() + " " + u;
                }

                message += TextFormat.GOLD + "Usage: " + TextFormat.WHITE + usage + "\n";
                source.sendMessage(message);
                return 1;
            }
        }

        //source.sendMessage(TextFormat.RED + "No help for " + command.toLowerCase());
        return 1;
    }
}

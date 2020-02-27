package cn.nukkit.command.defaults;

import cn.nukkit.command.*;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.player.IPlayer;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Map;
import java.util.TreeMap;

import static cn.nukkit.command.args.OfflinePlayerArgument.getOfflinePlayer;
import static cn.nukkit.command.args.OfflinePlayerArgument.offlinePlayer;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class HelpCommand extends BaseCommand {

    public HelpCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("help", "%nukkit.command.help.description");
        setPermission("nukkit.command.help");

        dispatcher.register(literal("nkhelp") // temporary until i send the commands to the client
                .then(argument("page", integer()).executes(ctx -> run(ctx, getInteger(ctx, "page"))))
                // TODO: The following line will "overwrite" the page argument above.
                //       Find out a way to make them play together
                //.then(argument("command", string()).executes(ctx -> command(ctx, getString(ctx, "command"))))
                .executes(ctx -> run(ctx, 1)));
    }

    public int run(CommandContext<CommandSource> context, int page) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        if (!this.testPermission(source)) {
            return -1;
        }

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
        if (source instanceof ConsoleCommandSender) {
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

    public int command(CommandContext<CommandSource> context, String command) {
        CommandSource source = context.getSource();

        BaseCommand cmd = source.getServer().getCommandDispatcher().getCommand(command.toLowerCase());
        if (cmd != null) {
            if (cmd.testPermissionSilent(source)) {
                String message = TextFormat.YELLOW + "--------- " + TextFormat.WHITE + " Help: /" + cmd.getName() + TextFormat.YELLOW + " ---------\n";
                message += TextFormat.GOLD + "Description: " + TextFormat.WHITE + cmd.getDescription() + "\n";
                String usage = "";
                String[] usages = cmd.getUsage().split("\n");
                for (String u : usages) {
                    if (!usage.equals("")) {
                        usage += "\n" + TextFormat.WHITE;
                    }
                    usage += u;
                }
                message += TextFormat.GOLD + "Usage: " + TextFormat.WHITE + usage + "\n";
                source.sendMessage(message);
                return 1;
            }
        }

        source.sendMessage(TextFormat.RED + "No help for " + command.toLowerCase());
        return 1;
    }
}

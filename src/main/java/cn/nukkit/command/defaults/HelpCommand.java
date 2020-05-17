package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.Map;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", CommandData.builder("help")
                .setDescription("commands.help.description")
                .setUsageMessage("/help [command|page]")
                .addAlias("?")
                .setPermissions("nukkit.command.help")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("page", CommandParamType.INT, true)
                }, new CommandParameter[]{
                        new CommandParameter("command", CommandParamType.COMMAND, true)
                })
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        int pageNumber = 1;
        int pageHeight = 5;
        String command = "";
        if (args.length != 0) {
            try {
                pageNumber = Integer.parseInt(args[0]);
                if (pageNumber <= 0) {
                    pageNumber = 1;
                }
            } catch (NumberFormatException e) {
                command = args[0];
            }
        }

        if (sender instanceof ConsoleCommandSender) {
            pageHeight = Integer.MAX_VALUE;
        }

        if (command.length() == 0) {
            Map<String, Command> commands = new TreeMap<>();
            for (Command cmd : sender.getServer().getCommandRegistry().getRegisteredCommands().values()) {
                if (cmd.testPermissionSilent(sender)) {
                    commands.put(cmd.getName(), cmd);
                }
            }
            int totalPage = commands.size() % pageHeight == 0 ? commands.size() / pageHeight : commands.size() / pageHeight + 1;
            pageNumber = Math.min(pageNumber, totalPage);
            if (pageNumber < 1) {
                pageNumber = 1;
            }

            sender.sendMessage(new TranslationContainer("commands.help.header", pageNumber, totalPage));
            int i = 1;
            for (Command command1 : commands.values()) {
                if (i >= (pageNumber - 1) * pageHeight + 1 && i <= Math.min(commands.size(), pageNumber * pageHeight)) {
                    sender.sendMessage(TextFormat.DARK_GREEN + "/" + command1.getName() + ": "
                            + TextFormat.WHITE + sender.getServer().getLanguage().translate(command1.getDescription()));
                }
                i++;
            }
        } else {
            Command cmd = sender.getServer().getCommandRegistry().getCommand(command.toLowerCase());
            if (cmd != null) {
                if (cmd.testPermissionSilent(sender)) {
                    String desc = sender.getServer().getLanguage().translate(cmd.getDescription());
                    String message = TextFormat.YELLOW + "--------- " + TextFormat.WHITE + " Help: /" + cmd.getName() + TextFormat.YELLOW + " ---------\n";
                    message += TextFormat.GOLD + "Description: " + TextFormat.WHITE + desc + "\n";
                    message += TextFormat.GOLD + "Usage: " + TextFormat.WHITE + cmd.getUsage() + "\n";
                    sender.sendMessage(message);
                    return true;
                }
            }
            sender.sendMessage(TextFormat.RED + "No help for " + command);
        }
        return true;
    }
}

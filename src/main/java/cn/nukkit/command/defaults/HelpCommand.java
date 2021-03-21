package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class HelpCommand extends VanillaCommand {

    public HelpCommand(String name) {
        super(name, "%nukkit.command.help.description", "%commands.help.usage", new String[]{"?"});
        this.setPermission("nukkit.command.help");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("page", true, CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        StringBuilder command = new StringBuilder();
        int pageNumber = 1;
        int pageHeight = 5;
        if (args.length != 0) {
            try {
                pageNumber = Integer.parseInt(args[args.length - 1]);
                if (pageNumber <= 0) {
                    pageNumber = 1;
                }

                String[] newargs = new String[args.length - 1];
                System.arraycopy(args, 0, newargs, 0, newargs.length);
                args = newargs;
                /*if (args.length > 1) {
                    args = Arrays.copyOfRange(args, 0, args.length - 2);
                } else {
                    args = new String[0];
                }*/
                for (String arg : args) {
                    if (!command.toString().equals("")) {
                        command.append(" ");
                    }
                    command.append(arg);
                }
            } catch (NumberFormatException e) {
                pageNumber = 1;
                for (String arg : args) {
                    if (!command.toString().equals("")) {
                        command.append(" ");
                    }
                    command.append(arg);
                }
            }
        }

        if (sender instanceof ConsoleCommandSender) {
            pageHeight = Integer.MAX_VALUE;
        }

        if (command.toString().equals("")) {
            Map<String, Command> commands = new TreeMap<>();
            for (Command cmd : sender.getServer().getCommandMap().getCommands().values()) {
                if (cmd.testPermissionSilent(sender)) {
                    commands.put(cmd.getName(), cmd);
                }
            }
            int totalPage = commands.size() % pageHeight == 0 ? commands.size() / pageHeight : commands.size() / pageHeight + 1;
            pageNumber = Math.min(pageNumber, totalPage);
            if (pageNumber < 1) {
                pageNumber = 1;
            }

            sender.sendMessage(new TranslationContainer("commands.help.header", String.valueOf(pageNumber), String.valueOf(totalPage)));
            int i = 1;
            for (Command command1 : commands.values()) {
                if (i >= (pageNumber - 1) * pageHeight + 1 && i <= Math.min(commands.size(), pageNumber * pageHeight)) {
                    sender.sendMessage(TextFormat.DARK_GREEN + "/" + command1.getName() + ": " + TextFormat.WHITE + command1.getDescription());
                }
                i++;
            }

            return true;
        } else {
            Command cmd = sender.getServer().getCommandMap().getCommand(command.toString().toLowerCase());
            if (cmd != null) {
                if (cmd.testPermissionSilent(sender)) {
                    String message = TextFormat.YELLOW + "--------- " + TextFormat.WHITE + " Help: /" + cmd.getName() + TextFormat.YELLOW + " ---------\n";
                    message += TextFormat.GOLD + "Description: " + TextFormat.WHITE + cmd.getDescription() + "\n";
                    StringBuilder usage = new StringBuilder();
                    String[] usages = cmd.getUsage().split("\n");
                    for (String u : usages) {
                        if (!usage.toString().equals("")) {
                            usage.append("\n" + TextFormat.WHITE);
                        }
                        usage.append(u);
                    }
                    message += TextFormat.GOLD + "Usage: " + TextFormat.WHITE + usage + "\n";
                    sender.sendMessage(message);
                    return true;
                }
            }

            sender.sendMessage(TextFormat.RED + "No help for " + command.toString().toLowerCase());
            return true;
        }
    }
}

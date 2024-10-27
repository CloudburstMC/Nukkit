package cn.nukkit.plugin.simple;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class SimpleCommandData {

    public SimpleCommandData(CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
    }

    private CommandSender sender;

    private cn.nukkit.command.Command command;

    private String label;

    private String[] args;

    public Command getCommand() {
        return command;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getLabel() {
        return label;
    }

    public String[] getArgs() {
        return args;
    }
}

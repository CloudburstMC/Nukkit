package cn.nukkit.command.args.builder;

import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import lombok.Getter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

@ToString
public class CommandOverloadBuilder {
    @Getter
    private List<CommandParameter> parameters = new LinkedList<>();

    public CommandOverloadBuilder literal(String name) {
        parameters.add(new CommandParameter(name, new String[]{name}));
        return this;
    }

    public CommandOverloadBuilder literals(String name, String enumName, String[] names) {
        parameters.add(new CommandParameter(name, enumName, false, names));
        return this;
    }

    public CommandOverloadBuilder requiredArg(String name, CommandParamType type) {
        parameters.add(new CommandParameter(name, type, false));
        return this;
    }

    // Used for client side enums mostly
    public CommandOverloadBuilder requiredArg(String name, String enumName) {
        parameters.add(new CommandParameter(name, enumName));
        return this;
    }

    public CommandOverloadBuilder requiredArg(String name, String[] enumValues) {
        parameters.add(new CommandParameter(name, enumValues));
        return this;
    }

    public CommandOverloadBuilder requiredArg(String name, String enumName, String[] enumValues) {
        parameters.add(new CommandParameter(name, enumName, false, enumValues));
        return this;
    }

    public CommandOverloadBuilder optionalArg(String name, CommandParamType type) {
        parameters.add(new CommandParameter(name, type, true));
        return this;
    }

    public CommandOverloadBuilder optionalArg(String name, String[] enumValues) {
        parameters.add(new CommandParameter(name, true, enumValues));
        return this;
    }
}

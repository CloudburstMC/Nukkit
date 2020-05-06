package cn.nukkit.command.args.builder;

import lombok.Getter;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

@ToString
public class CommandOverloadBuilder {

    @Getter
    private Map<String, ArgumentBuilder> arguments = new LinkedHashMap<>();

    public CommandOverloadBuilder then(ArgumentBuilder argument) {
        arguments.put(argument.getName(), argument);
        return this;
    }
}

package cn.nukkit.command.args.builder;

import lombok.Getter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

@ToString
public class CommandOverloadBuilder {

    @Getter
    private List<ArgumentBuilder> arguments = new LinkedList<>();

    public CommandOverloadBuilder then(ArgumentBuilder argument) {
        arguments.add(argument);
        return this;
    }
}

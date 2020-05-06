package cn.nukkit.command.args.builder;

import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import com.nukkitx.protocol.bedrock.data.CommandParamData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OptionalArgumentBuilder extends ArgumentBuilder<OptionalArgumentBuilder> {
    @Getter
    private final String name;
    private final CommandParameter parameter;

    @Override
    protected OptionalArgumentBuilder getThis() {
        return this;
    }

    public static OptionalArgumentBuilder optionalArg(String name, CommandParamType type) {
        return new OptionalArgumentBuilder(name, new CommandParameter(name, type, true));
    }

    public static OptionalArgumentBuilder optionalArg(String name, String enumName) {
        return new OptionalArgumentBuilder(name, new CommandParameter(name, true, enumName));
    }

    public static OptionalArgumentBuilder optionalArg(String name, String[] enumValues) {
        return new OptionalArgumentBuilder(name, new CommandParameter(name, true, enumValues));
    }

    public static OptionalArgumentBuilder optionalArg(String name, String enumName, String[] enumValues) {
        return new OptionalArgumentBuilder(name, new CommandParameter(name, enumName, true, enumValues));
    }

    public OptionalArgumentBuilder postfix(String postfix) {
        this.parameter.postFix = postfix;
        return this;
    }

    public OptionalArgumentBuilder expandEnum() {
        this.parameter.options.add(CommandParamData.Option.UNKNOWN_0);
        return this;
    }

    @Override
    public CommandParameter build() {
        return parameter;
    }
}

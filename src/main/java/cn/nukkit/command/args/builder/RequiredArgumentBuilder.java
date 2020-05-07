package cn.nukkit.command.args.builder;

import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import com.nukkitx.protocol.bedrock.data.CommandParamData;

public class RequiredArgumentBuilder extends ArgumentBuilder<RequiredArgumentBuilder> {
    private final CommandParameter parameter;

    public RequiredArgumentBuilder(String name, CommandParameter parameter) {
        super(name);
        this.parameter = parameter;
    }

    @Override
    protected RequiredArgumentBuilder getThis() {
        return this;
    }

    public static RequiredArgumentBuilder requiredArg(String name, CommandParamType type) {
        return new RequiredArgumentBuilder(name, new CommandParameter(name, type, false));
    }

    public static RequiredArgumentBuilder requiredArg(String name, String enumName) {
        return new RequiredArgumentBuilder(name, new CommandParameter(name, enumName));
    }

    public static RequiredArgumentBuilder requiredArg(String name, String[] enumValues) {
        return new RequiredArgumentBuilder(name, new CommandParameter(name, enumValues));
    }

    public static RequiredArgumentBuilder requiredArg(String name, String enumName, String[] enumValues) {
        return new RequiredArgumentBuilder(name, new CommandParameter(name, enumName, false, enumValues));
    }

    public RequiredArgumentBuilder postfix(String postfix) {
        this.parameter.setPostFix(postfix);
        return this;
    }

    @Override
    public CommandParameter build() {
        return parameter;
    }
}

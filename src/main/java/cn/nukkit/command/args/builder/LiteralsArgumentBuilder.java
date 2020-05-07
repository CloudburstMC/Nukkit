package cn.nukkit.command.args.builder;

import cn.nukkit.command.data.CommandParameter;
import com.nukkitx.protocol.bedrock.data.CommandParamData;

import java.util.LinkedList;
import java.util.List;

public class LiteralsArgumentBuilder extends ArgumentBuilder<LiteralsArgumentBuilder> {
    private CommandParameter parameter;

    public LiteralsArgumentBuilder(String name, String enumName, String[] names) {
        super(name);

        this.parameter = new CommandParameter(name, enumName, false, names);
    }

    @Override
    protected LiteralsArgumentBuilder getThis() {
        return this;
    }

    public static LiteralsArgumentBuilder literals(String name, String enumName, LiteralArgumentBuilder... literals) {
        List<String> names = new LinkedList<>();
        for (LiteralArgumentBuilder literal : literals) {
            names.add(literal.getName());
        }
        return new LiteralsArgumentBuilder(name, enumName, names.toArray(new String[0]));
    }

    public static LiteralsArgumentBuilder literals(String name, String enumName, String[] names) {
        return new LiteralsArgumentBuilder(name, enumName, names);
    }

    public LiteralsArgumentBuilder expandEnum() {
        this.parameter.getOptions().add(CommandParamData.Option.UNKNOWN_0);
        return this;
    }

    @Override
    public CommandParameter build() {
        return parameter;
    }
}

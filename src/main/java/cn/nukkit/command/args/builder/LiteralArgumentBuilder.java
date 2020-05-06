package cn.nukkit.command.args.builder;

import cn.nukkit.command.data.CommandParameter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LiteralArgumentBuilder extends ArgumentBuilder<LiteralArgumentBuilder> {
    private final String name;

    @Override
    protected LiteralArgumentBuilder getThis() {
        return this;
    }

    public static LiteralArgumentBuilder literal(String name) {
        return new LiteralArgumentBuilder(name);
    }

    @Override
    public CommandParameter build() {
        return new CommandParameter(name, new String[]{name});
    }
}

package cn.nukkit.command.args.builder;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.args.CommandExecutor;
import cn.nukkit.command.data.CommandParameter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public abstract class ArgumentBuilder<T extends ArgumentBuilder> {
    protected final String name;
    private CommandExecutor executor;

    private Predicate<CommandSender> requirement = sender -> true;

    protected abstract T getThis();

    public T requires(Predicate<CommandSender> requirement) {
        this.requirement = requirement;
        return getThis();
    }

    public T executes(CommandExecutor executor) {
        this.executor = executor;
        return getThis();
    }

    public abstract CommandParameter build();
}

package cn.nukkit.command.args.builder;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import java.util.function.Predicate;

public abstract class ArgumentBuilder<T extends ArgumentBuilder> {
    private String executor;

    private Predicate<CommandSender> requirement = sender -> true;

    protected abstract T getThis();

    public T requires(Predicate<CommandSender> requirement) {
        this.requirement = requirement;
        return getThis();
    }

    public abstract CommandParameter build();
}

package cn.nukkit.command.args;

import java.io.StringReader;

public interface  BaseArgument<T> {
    T parse(StringReader reader);
}

package cn.nukkit.command.args.basic;

import cn.nukkit.command.args.BaseArgument;

import java.io.StringReader;

public class IntegerArgument implements BaseArgument<Integer> {

    public static IntegerArgument integer() {
        return new IntegerArgument();
    }

    public static int getInteger(String name) {
        return 0;
    }

    @Override
    public Integer parse(StringReader reader) {

        return null;
    }
}

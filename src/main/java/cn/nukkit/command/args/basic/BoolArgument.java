package cn.nukkit.command.args.basic;

public class BoolArgument extends EnumArgument<Boolean> {

    public BoolArgument() {
        super("Boolean", new String[]{"true", "false"});
    }
}

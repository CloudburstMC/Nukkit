package cn.nukkit.api.command;

public interface AliasCommand extends Command {

    Command getParent();
}

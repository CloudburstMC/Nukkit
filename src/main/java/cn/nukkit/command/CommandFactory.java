package cn.nukkit.command;

@FunctionalInterface
public interface CommandFactory {

    Command create(String name);
}
